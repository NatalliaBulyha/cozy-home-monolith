package com.cozyhome.onlineshop.basketservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.basketservice.model.ShoppingCartLine;

public interface ShoppingCartLineRepository extends JpaRepository<ShoppingCartLine, Integer>{
	
	List<ShoppingCartLine> findByUserId(String userId);

}
