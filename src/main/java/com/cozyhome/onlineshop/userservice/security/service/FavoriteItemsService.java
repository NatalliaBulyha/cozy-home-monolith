package com.cozyhome.onlineshop.userservice.security.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.FavoriteItemDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;

public interface FavoriteItemsService {

	void updateUserFavoriteItems(String userId, ProductColorDto dtoRequest);
	
	List<FavoriteItemDto> getFavoriteItemsByUserId(String userId);
	
}
