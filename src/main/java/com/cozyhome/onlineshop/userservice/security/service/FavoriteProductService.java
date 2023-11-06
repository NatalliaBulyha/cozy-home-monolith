package com.cozyhome.onlineshop.userservice.security.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;

public interface FavoriteProductService {

	void updateUserFavoriteProducts(String userId, ProductColorDto dtoRequest);
	
	List<ProductDto> getFavoriteProductsByUserId(String userId, PageableDto pageable);
	
	List<ProductDto> getFavoriteProductsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable);
}
