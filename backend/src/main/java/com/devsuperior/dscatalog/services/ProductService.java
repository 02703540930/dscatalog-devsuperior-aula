package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entitites.Category;
import com.devsuperior.dscatalog.entitites.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true) // garantia das propriedades ACID
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);
		return list.map(x -> new ProductDTO(x));
	}

	// optional usado para evitar valores nulos vindos do findById
	@Transactional(readOnly = true) // garantia das propriedades ACID
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();          
		copyDtotoEntity(dto, entity);          // cria um metodo que relaciona os campos para ocupar no insert e update
										       // ou coloca os demais campos, caso tenha mais de um na entidade
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	// Método para atualizar um dado nas entidades e trata exceção
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getOne(id);     // instancia com getOne antes de salvar
			copyDtotoEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} 
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id); // criada no services.exceptions
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation"); // criada no services.exceptions
		}
	}

	// copiador dos dados dos atributos para insert e update //não precisa fazer
	// para os atributos chave
	private void copyDtotoEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());

		entity.getCategories().clear();     // limpa a lista de categorias anterior, caso tenha

		// busca o id de cada categoria
		for (CategoryDTO catDto : dto.getCategories()) { 
			Category category = categoryRepository.getOne(catDto.getId()); // import entities.category
			entity.getCategories().add(category);
		}
	}
}
