package com.cozyhome.onlineshop.dto;

import java.math.BigDecimal;
import java.util.List;

import com.cozyhome.onlineshop.dto.productcard.ColorQuantityStatusDto;

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
    private List<ImageDto> imageDtoList;
    private List<ColorQuantityStatusDto> colorDtoList;
}
