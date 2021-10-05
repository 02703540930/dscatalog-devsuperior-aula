package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entitites.Category;
import com.devsuperior.dscatalog.entitites.Product;

public class Factory {
	
	public static Category createCategory() {
		return new Category(2L,"Eletrinics");		
	}
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(createCategory());
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());		
	}
	



}
