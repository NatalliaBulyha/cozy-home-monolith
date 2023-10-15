package com.cozyhome.onlineshop.shoppingcartservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartDto;
import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartLineDto;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.model.enums.ProductQuantityStatus;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.repository.ColorRepository;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.shoppingcartservice.model.ShoppingCartLine;
import com.cozyhome.onlineshop.shoppingcartservice.repository.ShoppingCartLineRepository;
import com.cozyhome.onlineshop.shoppingcartservice.service.ShoppingCartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private final ShoppingCartLineRepository shoppingCartLineRepository;
	private final ProductColorRepository productColorRepository;
	private final ProductRepository productRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	
	private final ModelMapper modelMapper;
	
	@Value("${image.product.path.base}")
    private String imagePathBase;

	@Override
	public List<ShoppingCartDto> getShoppingCart(String userId) {
		List<ShoppingCartLine> list = shoppingCartLineRepository.findByUserId(userId);		
		return buildShoppingCartDtoList(list);
	}
	
	private List<ShoppingCartDto> buildShoppingCartDtoList(List<ShoppingCartLine> list){
		Map<ProductColorDto, ImageProduct> imageMap = getImageMap(list);
		List<ShoppingCartDto> result = new ArrayList<>();
		for(ShoppingCartLine line : list) {
			String skuCode = line.getProductColor().getProductSkuCode();
			String hex = line.getProductColor().getColorHex();
			ShoppingCartDto dto = buildShoppingCartDto(line, imageMap.get(new ProductColorDto(skuCode, hex)));
			result.add(dto);
		}
		return result;
	}
	
	private Map<ProductColorDto, ImageProduct> getImageMap(List<ShoppingCartLine> list){
		List<ProductColorDto> dtos = list.stream().map(line -> modelMapper.map(line.getProductColor(), ProductColorDto.class)).toList();
		return imageRepositoryCustom.findMainImagesByProductColorList(dtos);		
	}
	
	private ShoppingCartDto buildShoppingCartDto(ShoppingCartLine line, ImageProduct imageProduct) {
		Product product = productRepository.findBySkuCode(line.getProductColor().getProductSkuCode())
				.orElseThrow(()-> new IllegalArgumentException("No product found by skuCode " + line.getProductColor().getProductSkuCode()));		
	
		String imagePah = imagePathBase + imageProduct.getSliderImageName();
		ShoppingCartDto dto = ShoppingCartDto.builder()
				.skuCode(product.getSkuCode())
				.productName(product.getName())
				.price(product.getPrice())
				.imagePath(imagePah)
				.colorHex(line.getProductColor().getColorHex())
				.colorName(ColorsEnum.getColorNameByHex(line.getProductColor().getColorHex()))
				.quantity(line.getQuantity())
				.quantityStatus(ProductQuantityStatus.getStatusByQuantity(line.getQuantity()))
				.build();
		
		if(product.getDiscount()>0) {
			dto.setPriceWithDiscount(product.getPriceWithDiscount());
		}
		return dto;
	}

	@Override
	public void synchronizeShoppingCart(String userId, List<ShoppingCartLineDto> dtoList) {
	    List<ShoppingCartLine> existingShoppingCart = shoppingCartLineRepository.findByUserId(userId);

	    for (ShoppingCartLineDto newShoppingCartLine : dtoList) {
	        ProductColor productColor = productColorRepository.findByProductSkuCodeAndColorHex(newShoppingCartLine.getSkuCode(), newShoppingCartLine.getColorHex())
	                .orElseThrow(() -> new IllegalArgumentException("No product color found."));

	        Optional<ShoppingCartLine> existingLine = existingShoppingCart.stream()
	                .filter(shopingCartLine -> shopingCartLine.getProductColor().equals(productColor))
	                .findFirst();

	        if (existingLine.isPresent()) {
	            existingLine.get().setQuantity(newShoppingCartLine.getQuantity());
	        } else {
	            ShoppingCartLine shoppingCartLine = ShoppingCartLine.builder()
	                    .productColor(productColor)
	                    .quantity(newShoppingCartLine.getQuantity())
	                    .userId(userId)
	                    .build();
	            existingShoppingCart.add(shoppingCartLine);
	        }
	    }
	    
	    shoppingCartLineRepository.saveAll(existingShoppingCart);
	}
	
}
