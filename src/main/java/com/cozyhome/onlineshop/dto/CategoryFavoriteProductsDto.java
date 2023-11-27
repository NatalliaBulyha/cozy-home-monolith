package com.cozyhome.onlineshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryFavoriteProductsDto {

	private short countOfProducts;
	
    private short countOfPages;
    
    private List<ProductDto> products;
}
