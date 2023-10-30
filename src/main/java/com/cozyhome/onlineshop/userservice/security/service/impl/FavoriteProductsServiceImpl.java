package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.productservice.service.CategoryService;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;
import com.cozyhome.onlineshop.userservice.repository.FavoriteProductRepository;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteProductsService;
import com.cozyhome.onlineshop.userservice.security.service.builder.FavoriteProductsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteProductsServiceImpl implements FavoriteProductsService {

	private final FavoriteProductRepository favoriteProductsRepository;
	private final ProductColorRepository productColorRepository;
	private final CategoryService categoryService;
	private final FavoriteProductsBuilder favoriteProductsBuilder;

	@Override
	public void updateUserFavoriteProducts(String userId, ProductColorDto dtoRequest) {
		String productSkuCode = dtoRequest.getProductSkuCode();
		String colorHex = dtoRequest.getColorHex();

		Optional<ProductColor> productColor = productColorRepository.findByProductSkuCodeAndColorHex(productSkuCode,
				colorHex);
		if (!productColor.isPresent()) {
			log.error("[ON updateUserFavoriteItems] :: No ProductColor found with skuCode {} and hex {}",
					productSkuCode, colorHex);
			throw new DataNotFoundException(
					String.format("No ProductColor found with skuCode %s and hex %s", productSkuCode, colorHex));
		}

		Optional<FavoriteProduct> favoriteProduct = favoriteProductsRepository
				.findByProductColorAndUserId(productColor.get(), userId);

		if (favoriteProduct.isPresent()) {
			favoriteProductsRepository.delete(favoriteProduct.get());
			log.info(
					"[ON updateUserFavoriteProducts] :: Item with skuCode [{}] and color hex [{}] was deleted from user's [{}] favorite items.",
					productSkuCode, colorHex, userId);
		} else {
			favoriteProductsRepository.save(new FavoriteProduct(productColor.get(), userId));
			log.info(
					"[ON updateUserFavoriteProducts] :: Item with skuCode [{}] and color hex [{}] was added to user's [{}] favorite items.",
					productSkuCode, colorHex, userId);
		}
	}

	@Override
	public List<FavoriteProductDto> getFavoriteProductsByUserId(String userId, PageableDto pageable) {
		Page<FavoriteProduct> favoriteProducts = favoriteProductsRepository.findAllByUserId(userId,
				PageRequest.of(pageable.getPage(), pageable.getSize()));
		return favoriteProductsBuilder.buildFavoriteProductsDtoList(favoriteProducts.getContent());
	}

	@Override
	public List<FavoriteProductDto> getFavoriteProductsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable) {
		List<ObjectId> categoriesIds = categoryService.getCategoriesIdsByParentId(categoryId);
		if (categoriesIds.isEmpty()) {
			log.error(
					"[ON getRandomProductsByStatusAndCategoryId]:: Subcategory for category with id {} doesn't exist.",
					categoryId);
			throw new DataNotFoundException(
					String.format("Subcategories for category with id %s not found.", categoryId));
		}
		List<FavoriteProductDto> favoriteProductDtos = getFavoriteProductsByUserId(userId, pageable).stream()
				.filter(dto -> categoriesIds.stream().anyMatch(c -> c.equals(new ObjectId(dto.getCategoryId()))))
				.toList();

		return favoriteProductDtos;
	}
}
