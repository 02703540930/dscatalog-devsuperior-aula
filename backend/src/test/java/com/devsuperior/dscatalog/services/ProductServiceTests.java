package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entitites.Category;
import com.devsuperior.dscatalog.entitites.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks // no lugar do @Service
	private ProductService service;

	@Mock // no lugar do @Autowired
	private ProductRepository repository;

	@Mock // no lugar do @Autowired
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	private ProductDTO productDTO;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;

		// product e page para simular uma pagina de produtos do findAll(pageable)
		product = Factory.createProduct();
		category = Factory.createCategory();
		productDTO = Factory.createProductDTO();

		page = new PageImpl<>(List.of(product));

		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		// simula o comportamento do repository para busca pelo Id quando existe e qdo
		// não existe o ID
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// simula o comportamento do repository no update do product
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// simula o comportamento do repository no update do category
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.doNothing().when(repository).deleteById(existingId);

		// comportamento simulado para passar uma excessão quando o id não existe.
		// excessão lá do ProductService.java
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId); // verifica se o metodo delete do service
																				// deste teste foi chamado (1) vez
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionDoNothingWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId); // verifica se o metodo delete do
																				// service
																				// deste teste foi chamado (1) vez
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionDoNothingWhenIdDoesNotExists() {

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId); // verifica se o metodo delete do service
																				// deste teste foi chamado (1) vez
	}

	@Test
	public void findAllPagedShouldReturnPage() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);

		Mockito.verify(repository).findAll(pageable); // verifica se o metodo delete do service

	}

	@Test
	public void findbyIdShouldReturnProductDtoWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.findById(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(existingId); // verifica se o metodo delete do service
																			// deste teste foi chamado (1) vez
	}

	// exercício
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() {

		ProductDTO result = service.findById(existingId);

		Assertions.assertNotNull(result);

		Mockito.verify(repository).findById(existingId); // verifica se o metodo delete do service

	}

	// exercício
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionDoNothingWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId); // verifica se o metodo delete do
																				// service
																				// deste teste foi chamado (1) vez
	}

	// exercicio
	@Test
	public void updadeShouldReturnProductDTOWhenIdExist() {

		ProductDTO result = service.update(existingId, productDTO);

		Assertions.assertNotNull(result);
	}

	// exercício
	@Test
	public void updateShouldThrowResourceNotFoundExceptionDoNothingWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});

	}
}
