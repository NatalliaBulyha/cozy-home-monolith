package com.cozyhome.onlineshop.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FavoriteItemDto {

	private String skuCode;
    private String productName;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private String imagePath;
    private String colorHex;
    private String colorName;
    private String quantityStatus;	
}
