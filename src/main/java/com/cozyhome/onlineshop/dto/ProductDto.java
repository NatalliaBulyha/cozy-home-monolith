package com.cozyhome.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private String skuCode;
    private String name;
    private String shortDescription;
    private BigDecimal price;
    private Byte discount;
    private BigDecimal priceWithDiscount;
    private List<ImageDto> imageDtoList;
    private List<ColorDto> colorDtoList;
    private String productQuantityStatus;

}
