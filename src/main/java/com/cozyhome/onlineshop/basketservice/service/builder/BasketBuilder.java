package com.cozyhome.onlineshop.basketservice.service.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cozyhome.onlineshop.basketcartservice.model.BasketRecord;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketDto;
import com.cozyhome.onlineshop.dto.shoppingcart.BasketRecordDto;
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

	public List<BasketDto> buildBasketDtoList(List<BasketRecord> list){
		Map<ProductColorDto, ImageProduct> imageMap = getImageMap(list);
		List<BasketDto> result = new ArrayList<>();
		for(BasketRecord line : list) {
			String skuCode = line.getProductColor().getProductSkuCode();
			String hex = line.getProductColor().getColorHex();
			BasketDto dto = buildBasketDto(line, imageMap.get(new ProductColorDto(skuCode, hex)));
			result.add(dto);
		}
		return result;
	}
	
	private Map<ProductColorDto, ImageProduct> getImageMap(List<BasketRecord> list){
		List<ProductColorDto> dtos = list.stream().map(line -> modelMapper.map(line.getProductColor(), ProductColorDto.class)).toList();
		return imageRepositoryCustom.findMainImagesByProductColorList(dtos);		
	}
	
	private BasketDto buildBasketDto(BasketRecord line, ImageProduct imageProduct) {
		Product product = productRepository.findBySkuCode(line.getProductColor().getProductSkuCode())
				.orElseThrow(()-> new IllegalArgumentException("No product found by skuCode " + line.getProductColor().getProductSkuCode()));		
	
		String imagePah = imagePathBase + imageProduct.getSliderImageName();
		BasketDto dto = BasketDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.price(product.getPrice())
				.imagePath(imagePah)
				.colorHex(line.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(line.getProductColor().getColorHex()))
				.quantity(line.getQuantity())
				.quantityStatus(ProductQuantityStatus.getStatusByQuantity(line.getQuantity()))
				.build();
		
		if(product.getDiscount() > 0) {
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
	}
	
    public List<BasketRecord> buildBasketRecordList(String userId, List<BasketRecordDto> dtoList){
		List<BasketRecord> result = new ArrayList<>();
		for(BasketRecordDto dto : dtoList) {
			ProductColor productColor = productColorRepository.findByProductSkuCodeAndColorHex(dto.getSkuCode(), dto.getColorHex())
					.orElseThrow(() -> new IllegalArgumentException("No productColor found."));
			BasketRecord basketRecord = BasketRecord.builder()
					.productColor(productColor)
					.quantity(dto.getQuantity())
					.userId(userId)
					.build();
			result.add(basketRecord);
		}
		return result;
	}
}
