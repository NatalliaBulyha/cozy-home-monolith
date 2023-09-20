package com.cozyhome.onlineshop.productservice.service.builder;

import com.cozyhome.onlineshop.dto.inventory.CheckingProductAvailableAndStatusDto;
import com.cozyhome.onlineshop.dto.CollectionDto;
import com.cozyhome.onlineshop.dto.ColorDto;
import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.ProductForBasketDto;
import com.cozyhome.onlineshop.dto.inventory.QuantityStatusDto;
import com.cozyhome.onlineshop.dto.productcard.ColorQuantityStatusDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardDto;
import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import com.cozyhome.onlineshop.productservice.model.Category;
import com.cozyhome.onlineshop.productservice.model.Color;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Material;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ColorsEnum;
import com.cozyhome.onlineshop.productservice.model.enums.ProductQuantityStatus;
import com.cozyhome.onlineshop.productservice.repository.CategoryRepository;
import com.cozyhome.onlineshop.productservice.repository.ImageProductRepository;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductBuilder {
	private final ImageBuilder imageBuilder;
	private final ModelMapper modelMapper;
	private final CategoryRepository categoryRepository;
	private final ImageRepositoryCustom imageRepositoryCustom;
	private final ImageProductRepository imageProductRepository;
	private final ReviewService reviewService;
	private final InventoryService inventoryService;

	private static final BigDecimal NULL_PERCENT = new BigDecimal("0");
	private static final DecimalFormat ROUND_ONE_PLACE = new DecimalFormat("0.0");

    @Value("${digits.after.decimal}")
    private int digitsAfterDecimal;
    @Value("${image.product.path.base}")
    private String imagePathBase;

	public List<ProductDto> buildProductDtoList(List<Product> products, boolean isMain) {
		List<String> productsSkuCodes = extractSkuCodes(products);
		Map<String, List<Color>> productColors = imageRepositoryCustom.groupColorsByProductSkuCodeIn(productsSkuCodes);
		List<ImageProduct> images = imageRepositoryCustom.findImagesByMainPhotoAndProductSkuCodeIn(productsSkuCodes,
				isMain);
		Map<String, List<ImageProduct>> imageMap = getImageMap(images);
		Map<String, String> quantityStatusMap = getProductQuantityStatusMap(products);
		List<ProductDto> productDtoList = new ArrayList<>();
		for (Product product : products) {
			String productSkuCode = product.getSkuCode();
			ProductDto dto = buildProductDto(product, imageMap.get(productSkuCode), productColors.get(productSkuCode));
			dto.setProductQuantityStatus(quantityStatusMap.get(productSkuCode));
			productDtoList.add(dto);
			log.info("PRODUCT DTO[" + dto + "]");
		}
		return productDtoList;
	}

	private List<String> extractSkuCodes(List<Product> products) {
		return products.stream().map(Product::getSkuCode).toList();
	}

	private Map<String, String> getProductQuantityStatusMap(List<Product> products) {
		Map<String, String> result = inventoryService.getQuantityStatusBySkuCodeList(extractSkuCodes(products));
		log.info("GET PRODUCT QUANTITY STATUS MAP " + result);
		return result;
	}

	public ProductDto buildProductDto(Product product, List<ImageProduct> images, List<Color> colors) {
		ProductDto productDto = ProductDto.builder().skuCode(product.getSkuCode()).name(product.getName())
				.shortDescription(product.getShortDescription())
				.price(roundBigDecimalToZeroDecimalPlace(product.getPrice()))
				.imageDtoList(imageBuilder.buildImageDtoList(images))
				.productQuantityStatus(ProductQuantityStatus.getDescriptionForFalseAvailability(product.isAvailable()))
				.build();

		if(colors != null) {
			productDto.setColorDtoList(colors.stream().map(color -> modelMapper.map(color, ColorDto.class)).toList());
		}
		BigDecimal discount = BigDecimal.valueOf(product.getDiscount());
		if (!discount.equals(NULL_PERCENT)) {
			productDto.setDiscount(product.getDiscount());
			productDto.setPriceWithDiscount(roundBigDecimalToZeroDecimalPlace(product.getPriceWithDiscount()));
		}
		return productDto;
	}

	public ProductCardDto buildProductCardDto(Product product, String colorId) {
		final String productSkuCode = product.getSkuCode();

		String categoryName = categoryRepository.findById(product.getSubCategory().getParentId()).map(Category::getName)
				.orElse("");

		ProductCardDto productCardDto = ProductCardDto.builder().categoryName(categoryName)
				.subCategoryName(product.getSubCategory().getName()).skuCode(productSkuCode).name(product.getName())
				.description(product.getDescription()).price(roundBigDecimalToZeroDecimalPlace(product.getPrice()))
				.materials(product.getMaterials().stream().map(Material::getName).toList())
				.collection(modelMapper.map(product.getCollection(), CollectionDto.class))
				.weight(roundFloatToOneDecimalPlace(product.getWeight()))
				.height(roundFloatToOneDecimalPlace(product.getHeight()))
				.width(roundFloatToOneDecimalPlace(product.getWidth()))
				.depth(roundFloatToOneDecimalPlace(product.getDepth())).maxLoad(product.getMaxLoad()).build();

		BigDecimal discount = BigDecimal.valueOf(product.getDiscount());
		if (!discount.equals(NULL_PERCENT)) {
			productCardDto.setDiscount(product.getDiscount());
			productCardDto.setPriceWithDiscount(roundBigDecimalToZeroDecimalPlace(product.getPriceWithDiscount()));
		}

		buildAdditionalCharacteristics(product, productCardDto);
		buildProductCardDetails(productSkuCode, productCardDto, colorId);

		return productCardDto;
	}

	private void buildProductCardDetails(String productSkuCode, ProductCardDto productCardDto, String colorId) {
		List<ImageProduct> images = imageProductRepository.findByProductSkuCodeAndColorId(productSkuCode, colorId);
		if (!images.isEmpty()) {
			productCardDto.setImages(imageBuilder.buildProductCardImageDtos(images));
		}

		QuantityStatusDto colorsQuantityStatus = inventoryService
				.getProductCardColorQuantityStatus(productSkuCode);
		if (colorsQuantityStatus.getStatus() != null) {
			productCardDto.setQuantityStatus(colorsQuantityStatus.getStatus());
		}

		if (!colorsQuantityStatus.getColorQuantityStatus().isEmpty()) {
			productCardDto.setColors(convertMapToColorDtoList(colorsQuantityStatus.getColorQuantityStatus()));
		}

		List<ReviewDto> reviews = reviewService.getReviewsForProduct(productSkuCode);
		if (!reviews.isEmpty()) {
			productCardDto.setReviews(reviews);
			productCardDto.setCountOfReviews((byte) reviews.size());
			productCardDto.setAverageRating(roundFloatToOneDecimalPlace(countAverageRating(reviews)));
		}
    }

	private List<ColorQuantityStatusDto> convertMapToColorDtoList(Map<String, String> map) {
		return map.entrySet().stream().map(
				entry -> new ColorQuantityStatusDto(entry.getKey(), ColorsEnum.getColorNameByHex(entry.getKey()), entry.getValue()))
				.toList();
	}

	private void buildAdditionalCharacteristics(Product product, ProductCardDto productCardDto) {
		if (product.getNumberOfDoors() != null) {
			productCardDto.setNumberOfDoors(product.getNumberOfDoors());
		}
		if (product.getNumberOfDrawers() != null) {
			productCardDto.setNumberOfDrawers(product.getNumberOfDrawers());
		}
		if (product.getBedLength() != null) {
			productCardDto.setBedLength(product.getBedLength());
		}
		if (product.getBedWidth() != null) {
			productCardDto.setBedWidth(product.getBedWidth());
		}
		if (product.getTransformation() != null) {
			productCardDto.setTransformation(product.getTransformation());
		}
		if (product.getHeightRegulation() != null) {
			productCardDto.setHeightAdjustment(product.getHeightRegulation());
		}
    }

    public List<ProductForBasketDto> buildProductsShopCard(Map<String, Product> productsMap, Map<ProductColorDto, ImageProduct> imagesMap,
														   List<ProductColorDto> productColorDtos,
														   Map<ProductColorDto, CheckingProductAvailableAndStatusDto> productAvailableAndStatus) {
        List<ProductForBasketDto> productShopCards = new ArrayList<>();
        productColorDtos.forEach(productColor -> {
            BigDecimal discount = BigDecimal.valueOf(productsMap.get(productColor.getProductSkuCode()).getDiscount());
            ProductForBasketDto productShopCard = ProductForBasketDto.builder()
                .skuCode(productColor.getProductSkuCode())
                .name(productsMap.get(productColor.getProductSkuCode()).getName())
                .price(productsMap.get(productColor.getProductSkuCode()).getPrice())
                .colorName(ColorsEnum.getColorNameByHex(productColor.getColorHex()))
				.colorHex(productColor.getColorHex())
                .availableProductQuantity(productAvailableAndStatus.get(productColor).getAvailableProductQuantity())
                .quantityStatus(productAvailableAndStatus.get(productColor).getQuantityStatus())
                .build();

            if (!discount.equals(NULL_PERCENT)) {
                productShopCard.setPriceWithDiscount(roundBigDecimalToZeroDecimalPlace(productsMap.get(productColor.getProductSkuCode()).getPriceWithDiscount()));
            }

            if (imagesMap.get(productColor) != null) {
                productShopCard.setImagePath(imagePathBase + imagesMap.get(productColor).getSliderImageName());
            }

            productShopCards.add(productShopCard);
        });

        return productShopCards;
    }

    private float countAverageRating(List<ReviewDto> reviews) {
        return (float) reviews.stream().mapToInt(ReviewDto::getRating).average().getAsDouble();
    }

	private Float roundFloatToOneDecimalPlace(Float floatValue) {
		Float roundedFloat = 0.0f;
		String format = "%.0f";
		if (floatValue != null) {
//			roundedFloat = Float.valueOf(ROUND_ONE_PLACE.format(floatValue));
			roundedFloat = Float.valueOf(String.format(format, floatValue));
		}
		return roundedFloat;
	}

	private BigDecimal roundBigDecimalToZeroDecimalPlace(BigDecimal value) {
		return value.setScale(digitsAfterDecimal, RoundingMode.HALF_UP);
	}

	private Map<String, List<ImageProduct>> getImageMap(List<ImageProduct> images) {
		return images.stream()
				.collect(Collectors.groupingBy(image -> image.getProduct().getSkuCode(), Collectors.toList()));
	}
}
