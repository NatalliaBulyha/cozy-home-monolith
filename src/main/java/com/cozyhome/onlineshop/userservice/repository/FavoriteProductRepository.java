package com.cozyhome.onlineshop.userservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Integer>{

	
	Page<FavoriteProduct> findAllByUserId(String userId, Pageable pageable);
	
	Optional<FavoriteProduct> findByProductColorAndUserId(ProductColor productColor, String userId);

}
