package com.devsuperior.dscatalog.entitites;

import java.io.Serializable;
import java.util.Objects;

//serializable é um recurso que transforma os dados em bytes para melhor trafego em rede
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	
	public Category() {
		
	}

	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	
    //IMPLEMENTACAO PADRAO PARA COMPARAR A CATEGORIA BASEADA NA CHAVE
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}

	

	
}
