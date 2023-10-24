package com.cozyhome.onlineshop.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.userservice.model.FavoriteItem;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Integer>{

	
	List<FavoriteItem> findAllByUserId(String userId);
	
	Optional<FavoriteItem> findByProductColorAndUserId(ProductColor productColor, String userId);
}
