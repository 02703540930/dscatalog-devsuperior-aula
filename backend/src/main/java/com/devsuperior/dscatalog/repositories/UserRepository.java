package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entitites.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);  //busca por email para validar se existe igual no banco
}
