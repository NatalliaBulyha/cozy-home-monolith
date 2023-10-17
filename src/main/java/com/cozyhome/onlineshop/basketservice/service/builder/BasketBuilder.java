package com.cozyhome.onlineshop.basketservice.service.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cozyhome.onlineshop.basketservice.model.BasketItem;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketItemDto;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.model.enums.ProductQuantityStatus;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BasketBuilder {	
	
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final ProductColorRepository productColorRepository;
	
	private final ModelMapper modelMapper;
	
	@Value("${image.product.path.base}")
    private String imagePathBase;

	public List<BasketDto> buildBasketDtoList(List<BasketItem> list){
		Map<ProductColorDto, ImageProduct> imageMap = getImageMap(list);
		List<BasketDto> basketDtoList = new ArrayList<>();
		for(BasketItem line : list) {
			String skuCode = line.getProductColor().getProductSkuCode();
			String hex = line.getProductColor().getColorHex();
			BasketDto dto = buildBasketDto(line, imageMap.get(new ProductColorDto(skuCode, hex)));
			basketDtoList.add(dto);
		}
		return basketDtoList;
	}
	
	private Map<ProductColorDto, ImageProduct> getImageMap(List<BasketItem> list){
		List<ProductColorDto> dtos = list.stream().map(line -> modelMapper.map(line.getProductColor(), ProductColorDto.class)).toList();
		return imageRepositoryCustom.findMainImagesByProductColorList(dtos);		
	}
	
	private BasketDto buildBasketDto(BasketItem basketItem, ImageProduct imageProduct) {
		Product product = productRepository.findBySkuCode(basketItem.getProductColor().getProductSkuCode())
				.orElseThrow(()-> new IllegalArgumentException("No product found by skuCode " + basketItem.getProductColor().getProductSkuCode()));		
	
		String imagePah = imagePathBase + imageProduct.getSliderImageName();
		BasketDto dto = BasketDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.price(product.getPrice())
				.imagePath(imagePah)
				.colorHex(basketItem.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(basketItem.getProductColor().getColorHex()))
				.quantity(basketItem.getQuantity())
				.quantityStatus(ProductQuantityStatus.getStatusByQuantity(basketItem.getQuantity()))
				.build();
		
		if(product.getDiscount() > 0) {
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
	}
	
    public List<BasketItem> buildBasketItemList(String userId, List<BasketItemDto> dtoList){   	
    	List<BasketItem> result = new ArrayList<>();
		for(BasketItemDto dto : dtoList) {
			ProductColor productColor = productColorRepository.findByProductSkuCodeAndColorHex(dto.getSkuCode(), dto.getColorHex())
					.orElseThrow(() -> new IllegalArgumentException("No productColor found."));
			BasketItem basketItem = BasketItem.builder()
					.productColor(productColor)
					.quantity(dto.getQuantity())
					.userId(userId)
					.build();
			result.add(basketItem);
		}
		return result;
	}
}
