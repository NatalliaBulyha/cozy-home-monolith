package com.cozyhome.onlineshop.basketservice.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.basket.BasketDto;
import com.cozyhome.onlineshop.dto.basket.BasketItemDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;

public interface BasketService {
	
	List<BasketDto> getBasket(String userId, PageableDto pageable);
	
	List<BasketDto> refreshBasket(String userId, List<BasketItemDto> dtoList, PageableDto pageable);
	
	void replaceBasket(String userId, List<BasketItemDto> dtoList);

}
