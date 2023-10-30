package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.productservice.service.CategoryService;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;
import com.cozyhome.onlineshop.userservice.repository.FavoriteProductRepository;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteProductsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteProductsServiceImpl implements FavoriteProductsService {

	private final FavoriteProductRepository favoriteProductsRepository;
	private final ProductColorRepository productColorRepository;
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final InventoryService inventoryService;
	private final CategoryService categoryService;

	private final ModelMapper modelMapper;

	@Value("${image.product.path.base}")
	private String imagePathBase;

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

		Optional<FavoriteProduct> favoriteProduct = favoriteProductsRepository.findByProductColorAndUserId(productColor.get(),
				userId);

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
		Page<FavoriteProduct> favoriteProducts = favoriteProductsRepository.findAllByUserId(userId, PageRequest.of(pageable.getPage(), pageable.getSize()));
		
		return buildFavoriteProductsDtoList(favoriteProducts.getContent());
	}

	private List<FavoriteProductDto> buildFavoriteProductsDtoList(List<FavoriteProduct> favoriteProductlist) {
		Map<ProductColorDto, ImageProduct> imageMap = getImageMap(favoriteProductlist);
		List<FavoriteProductDto> favoriteProductDtoList = new ArrayList<>();
		for (FavoriteProduct favoriteProduct : favoriteProductlist) {
			String skuCode = favoriteProduct.getProductColor().getProductSkuCode();
			String hex = favoriteProduct.getProductColor().getColorHex();
			FavoriteProductDto dto = buildFavoriteProductDto(favoriteProduct, imageMap.get(new ProductColorDto(skuCode, hex)));
			favoriteProductDtoList.add(dto);
		}
		return favoriteProductDtoList;
	}

	private Map<ProductColorDto, ImageProduct> getImageMap(List<FavoriteProduct> favoriteProductlist) {
		List<ProductColorDto> dtos = favoriteProductlist.stream()
				.map(line -> modelMapper.map(line.getProductColor(), ProductColorDto.class)).toList();
		return imageRepositoryCustom.findMainImagesByProductColorList(dtos);
	}

	private FavoriteProductDto buildFavoriteProductDto(FavoriteProduct favoriteProduct, ImageProduct imageProduct) {
		Product product = productRepository.findBySkuCode(favoriteProduct.getProductColor().getProductSkuCode())
				.orElseThrow(() -> new IllegalArgumentException(
						"No product found by skuCode " + favoriteProduct.getProductColor().getProductSkuCode()));

		String imagePah = imagePathBase + imageProduct.getSliderImageName();
		String quantityStatus = inventoryService.getQuantityStatusByProductColor(
				modelMapper.map(favoriteProduct.getProductColor(), ProductColorDto.class));

		FavoriteProductDto dto = FavoriteProductDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.shortDescription(product.getShortDescription())
				.price(product.getPrice()).imagePath(imagePah)
				.categoryId(product.getSubCategory().getId().toString())
				.colorHex(favoriteProduct.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(favoriteProduct.getProductColor().getColorHex()))
				.quantityStatus(quantityStatus)
				.build();

		if (product.getDiscount() > 0) {
			dto.setDiscount(product.getDiscount());
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
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
