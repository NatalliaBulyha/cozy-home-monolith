package com.cozyhome.onlineshop.dto.shoppingcart;

import com.cozyhome.onlineshop.validation.ValidColorHex;
import com.cozyhome.onlineshop.validation.ValidSkuCode;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ShoppingCartLineDto {

	@ValidSkuCode
	private String skuCode;
	
	@ValidColorHex
	private String colorHex;
	
	@Positive(message = "Quantity must be a positive number.")
	private short quantity;
}
