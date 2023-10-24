package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.FavoriteItemDto;
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
import com.cozyhome.onlineshop.userservice.model.FavoriteItem;
import com.cozyhome.onlineshop.userservice.repository.FavoriteItemRepository;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteItemsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FavoriteItemsServiceImpl implements FavoriteItemsService {

	private final FavoriteItemRepository favoriteItemsRepository;
	private final ProductColorRepository productColorRepository;
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final InventoryService inventoryService;

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
	public List<FavoriteItemDto> getFavoriteItemsByUserId(String userId) {
		List<FavoriteItem> items = favoriteItemsRepository.findAllByUserId(userId);
		return buildFavoriteItemDtoList(items);
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
				.price(product.getPrice()).imagePath(imagePah)
				.colorHex(favoriteItem.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(favoriteItem.getProductColor().getColorHex()))
				.quantityStatus(quantityStatus)
				.build();

		if (product.getDiscount() > 0) {
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
	}
}
