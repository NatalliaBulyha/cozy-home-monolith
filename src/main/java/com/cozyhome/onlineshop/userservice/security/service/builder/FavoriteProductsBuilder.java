package com.cozyhome.onlineshop.userservice.security.service.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FavoriteProductsBuilder {
	
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final InventoryService inventoryService;
	
	private final ModelMapper modelMapper;
	

	@Value("${image.product.path.base}")
	private String imagePathBase;
	
	public List<FavoriteProductDto> buildFavoriteProductsDtoList(List<FavoriteProduct> favoriteProductlist) {
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

}
