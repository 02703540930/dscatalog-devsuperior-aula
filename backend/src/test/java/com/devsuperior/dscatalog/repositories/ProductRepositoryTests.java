package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entitites.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;

	}

	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);

		product = repository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);

		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent()); // pois findbyid precisa retornar false
	}

	@Test
	public void deleteShouldTrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			repository.deleteById(nonExistingId);

		});
	}
	
	
	//exercicio
	@Test
	public void FindByIdShouldReturnOptmalProductnotEmptyWhenIdExists() {

		Optional<Product> result = repository.findById(existingId);

		Assertions.assertTrue(result.isPresent()); // findbyId precisa retornar true
	}
	
	@Test
	public void FindByIdShouldReturnOptmalProductEmptyWhenIdDoesNotExists() {

		Optional<Product> result = repository.findById(nonExistingId);

		Assertions.assertTrue(result.isEmpty()); // findbyId precisa retornar vazio
	}

}
