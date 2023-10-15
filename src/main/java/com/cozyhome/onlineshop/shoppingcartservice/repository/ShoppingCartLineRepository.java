package com.cozyhome.onlineshop.shoppingcartservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.shoppingcartservice.model.ShoppingCartLine;

public interface ShoppingCartLineRepository extends JpaRepository<ShoppingCartLine, Integer>{
	
	List<ShoppingCartLine> findByUserId(String userId);
	
//	void saveAll(List<ShoppingCartLine> list);

}
