package com.cozyhome.onlineshop.shoppingcartservice.service;

import java.util.List;

import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartDto;
import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartLineDto;

public interface ShoppingCartService {
	
	List<ShoppingCartDto> getShoppingCart(String userId);
	
	void synchronizeShoppingCart(String userId, List<ShoppingCartLineDto> dtoList);

}
