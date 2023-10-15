package com.cozyhome.onlineshop.basketservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.basketservice.model.ShoppingCartLine;
import com.cozyhome.onlineshop.basketservice.repository.ShoppingCartLineRepository;
import com.cozyhome.onlineshop.basketservice.service.ShoppingCartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{
	
	private final ShoppingCartLineRepository shoppingCartLineRepository;

	@Override
	public List<ShoppingCartLine> getShoppingCart(String userId) {
		
		return shoppingCartLineRepository.findByUserId(userId);
	}

}
