package com.cozyhome.onlineshop.userservice.security.service.builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.inventory.QuantityStatusDto;
import com.cozyhome.onlineshop.dto.productcard.ColorQuantityStatusDto;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.productservice.service.builder.ImageBuilder;
import com.cozyhome.onlineshop.userservice.model.FavoriteProduct;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FavoriteProductsBuilder {

	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final InventoryService inventoryService;
	private final ImageBuilder imageBuilder;

	@Value("${image.product.path.base}")
	private String imagePathBase;

	public List<FavoriteProductDto> buildFavoriteProductsDtoList(List<FavoriteProduct> favoriteProductlist) {
		List<String> productSkuCodes = favoriteProductlist.stream()
				.map(product -> product.getProductColor().getProductSkuCode()).toList();
		Map<String, List<ImageProduct>> imageMap = getImageMap(productSkuCodes);
		Map<String, QuantityStatusDto> quantityStatusMap = inventoryService
				.getQuantityStatusBySkuCodeList(productSkuCodes);
		List<Product> products = productRepository.findAllByStatusNotDeletedAndSkuCodeIn(productSkuCodes);
		
		return products.parallelStream() 
	            .map(product -> buildFavoriteProductDto(product, imageMap.get(product.getSkuCode()), quantityStatusMap.get(product.getSkuCode())))
	            .toList();
	}

	private Map<String, List<ImageProduct>> getImageMap(List<String> productSkuCodes) {
		List<ImageProduct> images = imageRepositoryCustom.findImagesByMainPhotoAndProductSkuCodeIn(productSkuCodes,
				true);
		return images.stream()
				.collect(Collectors.groupingBy(image -> image.getProduct().getSkuCode(), Collectors.toList()));
	}

	private FavoriteProductDto buildFavoriteProductDto(Product product, List<ImageProduct> images,
			QuantityStatusDto quantityStatus) {
		FavoriteProductDto dto = FavoriteProductDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.shortDescription(product.getShortDescription())
				.price(product.getPrice())
				.categoryId(product.getSubCategory().getId().toString())
				.imageDtoList(imageBuilder.buildImageDtoList(images))
				.build();

		if (product.getDiscount() > 0) {
			dto.setDiscount(product.getDiscount());
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}

		if (quantityStatus.getColorQuantityStatus() != null) {
			dto.setColorDtoList(convertMapToColorDtoList(quantityStatus.getColorQuantityStatus()));
		}
		return dto;
	}

	private List<ColorQuantityStatusDto> convertMapToColorDtoList(Map<String, String> map) {
		return map.entrySet().stream().map(entry -> new ColorQuantityStatusDto(entry.getKey(),
				ColorsEnum.getColorNameByHex(entry.getKey()), entry.getValue())).toList();
	}

}
