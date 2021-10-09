package com.devsuperior.dscatalog.entitites;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

//serializable é um recurso que transforma os dados em bytes para melhor trafego em rede
@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	
	@Column (columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")  // Recomendação com o padrão UTC - Greenwitch
	private Instant createdAt;
	
	@Column (columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")  // Recomendação com o padrão UTC - Greenwitch
	private Instant updatedAt;
	
	@ManyToMany(mappedBy = "categories")  //junção mapeada do join realizado na entidade products
	Set<Product> products =  new HashSet<>();
		
	public Set<Product> getProducts() {
		return products;
	}

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

	
	
	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdateddAt() {
		return updatedAt;
	}
	
	@PrePersist
	public void prePersist() {
		createdAt = Instant.now();
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedAt = Instant.now();
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
