package com.cozyhome.onlineshop.basketservice.service;

import java.util.List;

import com.cozyhome.onlineshop.basketservice.model.ShoppingCartLine;

public interface ShoppingCartService {
	
	List<ShoppingCartLine> getShoppingCart(String userId);

}
