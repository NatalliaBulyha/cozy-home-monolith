package com.cozyhome.onlineshop.userservice.security.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;

public interface FavoriteProductsService {

	void updateUserFavoriteProducts(String userId, ProductColorDto dtoRequest);
	
	List<FavoriteProductDto> getFavoriteProductsByUserId(String userId, PageableDto pageable);
	
	List<FavoriteProductDto> getFavoriteProductsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable);
}
