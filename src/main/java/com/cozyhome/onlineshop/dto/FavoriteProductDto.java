package com.cozyhome.onlineshop.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FavoriteProductDto {

	private String skuCode;
    private String productName;
    private String shortDescription;
    private String categoryId;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private byte discount;
    private String imagePath;
    private String colorHex;
    private String colorName;
    private String quantityStatus;	
}
