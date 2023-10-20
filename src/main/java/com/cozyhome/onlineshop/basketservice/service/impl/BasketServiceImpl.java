package com.cozyhome.onlineshop.basketservice.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.basketservice.model.BasketItem;
import com.cozyhome.onlineshop.basketservice.repository.BasketRepository;
import com.cozyhome.onlineshop.basketservice.service.BasketService;
import com.cozyhome.onlineshop.basketservice.service.builder.BasketBuilder;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketItemDto;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BasketServiceImpl implements BasketService {

	private final BasketRepository basketRepository;
	private final ProductColorRepository productColorRepository;
	private final BasketBuilder basketBuilder;

	@Override
	public List<BasketDto> getBasket(String userId) {
		List<BasketItem> list = basketRepository.findByUserId(userId);		
		return basketBuilder.buildBasketDtoList(list);
	}	
	

	@Override
	public List<BasketDto> refreshBasket(String userId, List<BasketItemDto> newBasket) {
	    List<BasketItem> existingBasket = basketRepository.findByUserId(userId);

	    for (BasketItemDto newBasketItem : newBasket) {
	        ProductColor productColor = productColorRepository.findByProductSkuCodeAndColorHex(newBasketItem.getSkuCode(), newBasketItem.getColorHex())
	                .orElseThrow(() -> new IllegalArgumentException("No product color found."));

	        Optional<BasketItem> existingBasketItem = existingBasket.stream()
	                .filter(basketItem -> basketItem.getProductColor().equals(productColor))
	                .findFirst();

	        if (existingBasketItem.isPresent()) {
	            existingBasketItem.get().setQuantity(newBasketItem.getQuantity());
	        } else {
	            BasketItem basketItemToSave = BasketItem.builder()
	                    .productColor(productColor)
	                    .quantity(newBasketItem.getQuantity())
	                    .userId(userId)
	                    .build();
	            existingBasket.add(basketItemToSave);
	        }
	    }	    
	    basketRepository.saveAll(existingBasket);
	    return getBasket(userId);
	}

	@Override
	public void replaceBasket(String userId, List<BasketItemDto> dtoList) {
		basketRepository.deleteAllByUserId(userId);
		basketRepository.saveAll(basketBuilder.buildBasketItemList(userId, dtoList));		
	}		
}
