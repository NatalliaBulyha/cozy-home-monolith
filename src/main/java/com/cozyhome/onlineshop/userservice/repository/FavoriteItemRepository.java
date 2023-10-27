package com.cozyhome.onlineshop.userservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.userservice.model.FavoriteItem;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Integer>{

	
	Page<FavoriteItem> findAllByUserId(String userId, Pageable pageable);
	
	Optional<FavoriteItem> findByProductColorAndUserId(ProductColor productColor, String userId);

}
