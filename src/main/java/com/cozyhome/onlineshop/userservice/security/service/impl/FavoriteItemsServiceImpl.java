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

import com.cozyhome.onlineshop.dto.FavoriteItemDto;
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
import com.cozyhome.onlineshop.userservice.model.FavoriteItem;
import com.cozyhome.onlineshop.userservice.repository.FavoriteItemsRepository;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteItemsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteItemsServiceImpl implements FavoriteItemsService {

	private final FavoriteItemsRepository favoriteItemsRepository;
	private final ProductColorRepository productColorRepository;
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final InventoryService inventoryService;
	private final CategoryService categoryService;

	private final ModelMapper modelMapper;

	@Value("${image.product.path.base}")
	private String imagePathBase;

	@Override
	public void updateUserFavoriteItems(String userId, ProductColorDto dtoRequest) {
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

		Optional<FavoriteItem> favoriteItem = favoriteItemsRepository.findByProductColorAndUserId(productColor.get(),
				userId);

		if (favoriteItem.isPresent()) {
			favoriteItemsRepository.delete(favoriteItem.get());
			log.info(
					"[ON updateUserFavoriteItems] :: Item with skuCode [{}] and color hex [{}] was deleted from user's [{}] favorite items.",
					productSkuCode, colorHex, userId);
		} else {
			favoriteItemsRepository.save(new FavoriteItem(productColor.get(), userId));
			log.info(
					"[ON updateUserFavoriteItems] :: Item with skuCode [{}] and color hex [{}] was added to user's [{}] favorite items.",
					productSkuCode, colorHex, userId);			
		}
	}

	@Override
	public List<FavoriteItemDto> getFavoriteItemsByUserId(String userId, PageableDto pageable) {
		Page<FavoriteItem> items = favoriteItemsRepository.findAllByUserId(userId, PageRequest.of(pageable.getPage(), pageable.getSize()));
		
		return buildFavoriteItemDtoList(items.getContent());
	}

	private List<FavoriteItemDto> buildFavoriteItemDtoList(List<FavoriteItem> favoriteItemlist) {
		Map<ProductColorDto, ImageProduct> imageMap = getImageMap(favoriteItemlist);
		List<FavoriteItemDto> favoriteItemDtoList = new ArrayList<>();
		for (FavoriteItem favoriteItem : favoriteItemlist) {
			String skuCode = favoriteItem.getProductColor().getProductSkuCode();
			String hex = favoriteItem.getProductColor().getColorHex();
			FavoriteItemDto dto = buildFavoriteItemDto(favoriteItem, imageMap.get(new ProductColorDto(skuCode, hex)));
			favoriteItemDtoList.add(dto);
		}
		return favoriteItemDtoList;
	}

	private Map<ProductColorDto, ImageProduct> getImageMap(List<FavoriteItem> favoriteItemlist) {
		List<ProductColorDto> dtos = favoriteItemlist.stream()
				.map(line -> modelMapper.map(line.getProductColor(), ProductColorDto.class)).toList();
		return imageRepositoryCustom.findMainImagesByProductColorList(dtos);
	}

	private FavoriteItemDto buildFavoriteItemDto(FavoriteItem favoriteItem, ImageProduct imageProduct) {
		Product product = productRepository.findBySkuCode(favoriteItem.getProductColor().getProductSkuCode())
				.orElseThrow(() -> new IllegalArgumentException(
						"No product found by skuCode " + favoriteItem.getProductColor().getProductSkuCode()));

		String imagePah = imagePathBase + imageProduct.getSliderImageName();
		String quantityStatus = inventoryService.getQuantityStatusByProductColor(
				modelMapper.map(favoriteItem.getProductColor(), ProductColorDto.class));

		FavoriteItemDto dto = FavoriteItemDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.shortDescription(product.getShortDescription())
				.price(product.getPrice()).imagePath(imagePah)
				.categoryId(product.getSubCategory().getId().toString())
				.colorHex(favoriteItem.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(favoriteItem.getProductColor().getColorHex()))
				.quantityStatus(quantityStatus)
				.build();

		if (product.getDiscount() > 0) {
			dto.setDiscount(product.getDiscount());
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
	}

	@Override
	public List<FavoriteItemDto> getFavoriteItemsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable) {
		List<ObjectId> categoriesIds = categoryService.getCategoriesIdsByParentId(categoryId);
		if (categoriesIds.isEmpty()) {
			log.error(
					"[ON getRandomProductsByStatusAndCategoryId]:: Subcategory for category with id {} doesn't exist.",
					categoryId);
			throw new DataNotFoundException(
					String.format("Subcategories for category with id %s not found.", categoryId));
		}
		List<FavoriteItemDto> items = getFavoriteItemsByUserId(userId, pageable).stream()
				.filter(dto -> categoriesIds.stream().anyMatch(c -> c.equals(new ObjectId(dto.getCategoryId()))))
				.toList();
		
		return items;
	}
}
