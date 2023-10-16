package com.cozyhome.onlineshop.basketservice.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketRecordDto;

public interface BasketService {
	
	List<BasketDto> getBasket(String userId);
	
	void refreshBasket(String userId, List<BasketRecordDto> dtoList);
	
	void replaceBasketOnLogout(String userId, List<BasketRecordDto> dtoList);

}
