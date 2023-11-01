package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.productservice.service.builder.ProductBuilder;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;
import com.cozyhome.onlineshop.userservice.repository.FavoriteProductRepository;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteProductServiceImpl implements FavoriteProductService {

	private final FavoriteProductRepository favoriteProductsRepository;
	private final ProductColorRepository productColorRepository;
	private final ProductRepository productRepository;
	private final ProductBuilder productBuilder;

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
	public List<ProductDto> getFavoriteProductsByUserId(String userId, PageableDto pageable) {
		List<Product> products = getProductsByUserId(userId, pageable);
		return productBuilder.buildProductDtoList(products, true);
	}

	@Override
	public List<ProductDto> getFavoriteProductsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable) {	
		List<Product> products = getProductsByUserId(userId, pageable)
				.stream().filter(product -> categoryId.equals(product.getSubCategory().getParentId().toString()))
				.toList();
		return productBuilder.buildProductDtoList(products, true);
	}

	private List<Product> getProductsByUserId(String userId, PageableDto pageable) {
		Page<FavoriteProduct> favoriteProducts = favoriteProductsRepository.findAllByUserId(userId,
				PageRequest.of(pageable.getPage(), pageable.getSize()));
		List<String> productSkuCodes = favoriteProducts.getContent().stream()
				.map(product -> product.getProductColor().getProductSkuCode()).toList();
		List<Product> products = productRepository.findAllByStatusNotDeletedAndSkuCodeIn(productSkuCodes);
		return products;
	}
}
