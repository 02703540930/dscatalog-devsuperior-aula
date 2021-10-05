package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourcesTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;  //para criar um corpo Json na verificação do update

	private Long existingId;
	private Long nonExistingId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;

	private long dependentId;

	@BeforeEach
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 2L;

		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));

		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existingId); //204
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);	//404	
		doThrow(DatabaseException.class).when(service).delete(dependentId);    
		
	}
	

	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception{
		ProductDTO productDTO = Factory.createProductDTO();
		productDTO.setId(null);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = null;
		try {
			result = mockMvc.perform(post("/products", existingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());		
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = null;
		
			result = mockMvc.perform(delete("/products/{id}", existingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON));
			result.andExpect(status().isNoContent());
		
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = null;
		
			result = mockMvc.perform(delete("/products/{id}", nonExistingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON));
			result.andExpect(status().isNotFound());
			
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenIdIsDependent() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

			ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest()); // 403
	}

	@Test
	public void updateByIdShouldReturnProductWhenExistingId() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);		
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());  //verifica se existe um campo id no contexto do json
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenDoesNotExistingId() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
					.content(jsonBody)	
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	

	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products")
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());                      // isOk busca a mensagem 200, do html para confirmar que chegou no \products
											                    // com uma pagina tipo productDTO
	}

	@Test
	public void findByIdShouldReturnProductWhenExistingId() throws Exception{
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());  //verifica se existe um campo id no contexto do json
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenDoesNotExistingId() throws Exception{
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}
