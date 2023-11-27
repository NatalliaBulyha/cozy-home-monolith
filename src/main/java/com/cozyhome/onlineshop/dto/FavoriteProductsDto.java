package com.cozyhome.onlineshop.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FavoriteProductsDto {

	private short countOfProducts;
	
    private short countOfPages;
    
    private List<CategoryDto> categories;
    
    private List<ProductDto> products;
}
