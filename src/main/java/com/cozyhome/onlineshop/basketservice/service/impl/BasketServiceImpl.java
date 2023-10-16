package com.cozyhome.onlineshop.basketservice.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.basketcartservice.model.BasketRecord;
import com.cozyhome.onlineshop.basketservice.repository.BasketRepository;
import com.cozyhome.onlineshop.basketservice.service.BasketService;
import com.cozyhome.onlineshop.basketservice.service.builder.BasketBuilder;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketRecordDto;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasketServiceImpl implements BasketService {

	private final BasketRepository basketRepository;
	private final ProductColorRepository productColorRepository;
	private final BasketBuilder basketBuilder;

	@Override
	public List<BasketDto> getBasket(String userId) {
		List<BasketRecord> list = basketRepository.findByUserId(userId);		
		return basketBuilder.buildBasketDtoList(list);
	}	
	

	@Override
	public void refreshBasket(String userId, List<BasketRecordDto> newBasket) {
	    List<BasketRecord> existingBasket = basketRepository.findByUserId(userId);

	    for (BasketRecordDto newBasketRecord : newBasket) {
	        ProductColor productColor = productColorRepository.findByProductSkuCodeAndColorHex(newBasketRecord.getSkuCode(), newBasketRecord.getColorHex())
	                .orElseThrow(() -> new IllegalArgumentException("No product color found."));

	        Optional<BasketRecord> existingBasketRecord = existingBasket.stream()
	                .filter(basketRecord -> basketRecord.getProductColor().equals(productColor))
	                .findFirst();

	        if (existingBasketRecord.isPresent()) {
	            existingBasketRecord.get().setQuantity(newBasketRecord.getQuantity());
	        } else {
	            BasketRecord basketRecordToSave = BasketRecord.builder()
	                    .productColor(productColor)
	                    .quantity(newBasketRecord.getQuantity())
	                    .userId(userId)
	                    .build();
	            existingBasket.add(basketRecordToSave);
	        }
	    }	    
	    basketRepository.saveAll(existingBasket);
	}


	@Override
	public void replaceBasketOnLogout(String userId, List<BasketRecordDto> dtoList) {
		basketRepository.deleteAllByUserId(userId);
		basketRepository.saveAll(basketBuilder.buildBasketRecordList(userId, dtoList));		
	}		
}
