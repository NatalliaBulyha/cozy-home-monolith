package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.CategoryDto;
import com.cozyhome.onlineshop.dto.CategoryFavoriteProductsDto;
import com.cozyhome.onlineshop.dto.FavoriteProductsDto;
import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.productcard.ColorQuantityStatusDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import com.cozyhome.onlineshop.inventoryservice.repository.ProductColorRepository;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.repository.CategoryRepository;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.productservice.repository.ProductRepositoryCustom;
import com.cozyhome.onlineshop.productservice.service.builder.ProductBuilder;
import com.cozyhome.onlineshop.productservice.wrapper.IdWrapper;
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
	private final ProductRepositoryCustom productRepositoryCustom;
	private final ProductBuilder productBuilder;
	private final FavoriteProductRepository favoriteItemsRepository;
	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

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
	public FavoriteProductsDto getFavoriteProductsByUserId(String userId, PageableDto pageable) {
		Page<FavoriteProduct> favoriteProducts = favoriteProductsRepository.findAllByUserId(userId,
				PageRequest.of(pageable.getPage(), pageable.getSize()));	
		System.out.println("favorite products-------------" + favoriteProducts.getContent());
		
		List<Product> products = getProductsByFavorites(favoriteProducts.getContent());

		List<ObjectId> categoryIds = products.stream().map(product -> product.getSubCategory().getParentId()).toList();
		List<CategoryDto> categories = categoryRepository.findAllByActiveAndIdIn(true, categoryIds).stream()
				.map(category -> new CategoryDto(category.getId().toString(), category.getName())).toList();
		System.out.println("product color--------------------" + favoriteProducts.stream()
				.map(fp -> modelMapper.map(fp.getProductColor(), ProductColorDto.class)).toList());
		List<ProductDto> productDtoList =  productBuilder.buildFavoriteProductDtoList(products, favoriteProducts.stream()
				.map(fp -> modelMapper.map(fp.getProductColor(), ProductColorDto.class)).toList());
		
		FavoriteProductsDto result = FavoriteProductsDto.builder()
				.categories(categories)
				.products(productDtoList)
				.countOfPages((short) favoriteProducts.getTotalPages())
				.countOfProducts((short) favoriteProducts.getTotalElements())
				.build();
		
		return result;
	}	

	@Override
	public CategoryFavoriteProductsDto getFavoriteProductsByUserIdAndCategoryId(String userId, String categoryId,
			PageableDto pageable) {	
		List<FavoriteProduct> favoriteProducts = favoriteProductsRepository.findAllByUserId(userId);				
		List<String> skuCodesList = favoriteProducts.stream().map(fp -> fp.getProductColor().getProductSkuCode()).toList();
		System.out.println("skuCodes------------------" + skuCodesList);
		List<ObjectId> subCategoriesList = categoryRepository.findAllIdsOnlyByActiveAndParentId(true, new ObjectId(categoryId)).stream().map(IdWrapper::getId).toList();
		System.out.println("subcategories----------------" + subCategoriesList);
		
		Page<Product> products = productRepositoryCustom.getBySkuCodeInAndCategoryIdsIn(skuCodesList, subCategoriesList, buildPageable(pageable));
		System.out.println("products--------------------" + products.getContent());
		if(products.getContent().isEmpty()) {
			return new CategoryFavoriteProductsDto();
		}
		List<ProductDto> productDtoList = productBuilder.buildFavoriteProductDtoList(products.getContent(), favoriteProducts.stream()
				.map(favoriteProduct -> modelMapper.map(favoriteProduct.getProductColor(), ProductColorDto.class)).toList());
		
		return new CategoryFavoriteProductsDto((short) products.getTotalPages(), (short) products.getTotalElements(), productDtoList);
	}
	
	private Pageable buildPageable(PageableDto pageable) {
		return PageRequest.of(pageable.getPage(), pageable.getSize());
	}
	
	private List<Product> getProductsByFavorites(List<FavoriteProduct> favoriteProducts){
		List<String> productSkuCodes = favoriteProducts.stream()
				.map(product -> product.getProductColor().getProductSkuCode())
				.toList();		
		List<Product> products = productRepository.findAllByStatusNotDeletedAndSkuCodeIn(productSkuCodes);
		return products;
	}
	
//	private void markFavorites(List<FavoriteProduct> favoriteProducts, List<ProductDto> productDtoList) {
//		List<ProductColor> productColorList = favoriteProducts.stream().map(FavoriteProduct::getProductColor).toList();
//		
//		for(ProductColor productColor : productColorList) {
//			productDtoList.stream()
//	        .filter(product -> product.getSkuCode().equals(productColor.getProductSkuCode()))
//	        .findFirst() 
//	        .ifPresent(product -> {
//	            product.getColorDtoList().stream()
//	                .filter(color -> color.getId().equals(productColor.getColorHex()))
//	                .findFirst() 
//	                .ifPresent(color -> color.setFavorite(true));
//	        });
//		}
//	}
	
	@Override
	public void markFavoritesForUser(String userId, List<ProductDto> products) {
		products.parallelStream().forEach(productDto ->
        doMarkFavorites(userId, productDto.getSkuCode(), productDto.getColorDtoList()));
	}

	@Override
	public void markFavoritesForUser(String userId, ProductCardDto productCard) {
		doMarkFavorites(userId, productCard.getSkuCode(), productCard.getColors());
	}

	private void doMarkFavorites(String userId, String skuCode, List<ColorQuantityStatusDto> colorsDto) {
		Set<ProductColor> favoriteProductColors = favoriteItemsRepository.findAllByUserId(userId)
				.stream()
				.map(item -> item.getProductColor())
				.collect(Collectors.toSet());
		
		for (ColorQuantityStatusDto colorDto : colorsDto) {
			ProductColor productColor = new ProductColor(skuCode, colorDto.getId());
			colorDto.setFavorite(favoriteProductColors.contains(productColor));
		}
	}
}
