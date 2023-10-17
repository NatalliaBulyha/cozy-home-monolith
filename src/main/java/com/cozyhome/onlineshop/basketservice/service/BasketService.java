package com.cozyhome.onlineshop.basketservice.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketItemDto;

public interface BasketService {
	
	List<BasketDto> getBasket(String userId);
	
	List<BasketDto> refreshBasket(String userId, List<BasketItemDto> dtoList);
	
	void replaceBasket(String userId, List<BasketItemDto> dtoList);

}
