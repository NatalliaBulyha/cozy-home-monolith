package com.cozyhome.onlineshop.dto.shoppingcart;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShoppingCartDto {

	private String skuCode;
    private String productName;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private String imagePath;
    private String colorHex;
    private String colorName;
    private int quantity;
    private String quantityStatus;
}
