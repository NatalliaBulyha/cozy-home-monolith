package com.cozyhome.onlineshop.productservice.repository;

import com.cozyhome.onlineshop.dto.filter.FilterDto;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ProductStatus;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

	List<Product> getRandomByStatusAndInStock(ProductStatus status, int count);
	List<Product> getRandomByStatusAndCategoryIdAndInStock(ProductStatus status, List<ObjectId> categoriesIds, int count);
	List<Product> filterProductsByCriterias(FilterDto dto, Pageable page);
}
