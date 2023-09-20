package com.cozyhome.onlineshop.productservice.service.impl;

import com.cozyhome.onlineshop.dto.inventory.CheckingProductAvailableAndStatusDto;
import com.cozyhome.onlineshop.dto.inventory.InventoryForBasketDto;
import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.ProductStatusDto;
import com.cozyhome.onlineshop.dto.ProductForBasketDto;
import com.cozyhome.onlineshop.dto.filter.FilterDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.request.SortDto;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import com.cozyhome.onlineshop.productservice.model.ImageProduct;
import com.cozyhome.onlineshop.productservice.model.Product;
import com.cozyhome.onlineshop.productservice.model.enums.ProductStatus;
import com.cozyhome.onlineshop.productservice.repository.CategoryRepository;
import com.cozyhome.onlineshop.productservice.repository.ImageRepositoryCustom;
import com.cozyhome.onlineshop.productservice.repository.ProductRepository;
import com.cozyhome.onlineshop.productservice.repository.ProductRepositoryCustom;
import com.cozyhome.onlineshop.productservice.service.CategoryService;
import com.cozyhome.onlineshop.productservice.service.ProductService;
import com.cozyhome.onlineshop.productservice.service.builder.ProductBuilder;
import com.cozyhome.onlineshop.productservice.service.builder.ProductFilterParametersBuilder;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepositoryCustom productRepositoryCustom;
    private final ImageRepositoryCustom imageRepositoryCustom;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final ProductBuilder productBuilder;
    private final ProductFilterParametersBuilder productFilterParametersBuilder;
    private final InventoryService inventoryService;

    private final boolean isMain = true;

    private static final String DIRECTION_ASC = "asc";
    private static final Direction DIRECTION_FOR_DEFAULT_SORTING = Direction.DESC;
    private static final String FIELD_NAME_FOR_DEFAULT_SORTING = "available";
    private static final String FIELD_NAME_FOR_ADDITIONAL_SORTING = "_id";


    @Override
    public List<ProductStatusDto> getProductStatuses() {
        return Arrays.stream(ProductStatus.values()).filter(status -> status != ProductStatus.DELETED)
            .map(status -> new ProductStatusDto(status.toString(), status.getDescription())).toList();
    }

    @Override
    public List<ProductDto> getRandomProductsByStatus(Byte status, int productCount) {
        List<Product> products = productRepositoryCustom.getRandomByStatusAndInStock(ProductStatus.valueOfDescription(status),
            productCount);
        return productBuilder.buildProductDtoList(products, isMain);
    }

    @Override
    public List<ProductDto> getRandomProductsByStatusAndCategoryId(Byte status, String categoryId,
                                                                   int countOfProducts) {
        List<ObjectId> categoriesIds = categoryService.getCategoriesIdsByParentId(categoryId);
        List<Product> products = productRepositoryCustom.getRandomByStatusAndCategoryIdAndInStock(
            ProductStatus.valueOfDescription(status), categoriesIds, countOfProducts);
        return productBuilder.buildProductDtoList(products, isMain);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(String categoryId, PageableDto pageable) {
        boolean isSubcategory = categoryRepository.hasParentById(new ObjectId(categoryId));
        Page<Product> products;
        if (isSubcategory) {
            products = productRepository.findAllByStatusNotDeletedAndCategoryId(new ObjectId(categoryId),
                buildPageable(pageable, new SortDto(null, null)));
        } else {
            List<ObjectId> objectIds = categoryService.getCategoriesIdsByParentId(categoryId);
            products = productRepository.findAllByStatusNotDeletedAndCategoryIdIn(objectIds,
                buildPageable(pageable, new SortDto(null, null)));
        }
        return productBuilder.buildProductDtoList(products.getContent(), isMain);
    }

    @Override
    public List<ProductDto> getFilteredProducts(FilterDto filter, PageableDto pageable, SortDto sortDto) {
        Pageable currentPageable = buildPageable(pageable, sortDto);
        List<Product> products = productRepositoryCustom.filterProductsByCriterias(filter, currentPageable);
        return productBuilder.buildProductDtoList(products, isMain);
    }

    @Override
    public FilterDto getFilterParameters(FilterDto filter, byte size) {
        List<Product> filteredProducts = productRepositoryCustom.filterProductsByCriterias(filter, null);
        List<ObjectId> objectIds = categoryService.getCategoriesIdsByParentId(filter.getParentCategoryId());
        List<Product> notFilteredProducts = productRepository.findAllByStatusNotDeletedAndCategoryIdIn(objectIds);
        return productFilterParametersBuilder.buildFilterParameters(filteredProducts, notFilteredProducts, size);
    }

    @Override
    public ProductCardDto getProductCard(ProductColorDto dto) {
        Product product = productRepository.findBySkuCode(dto.getProductSkuCode())
            .orElseThrow(() -> new IllegalArgumentException("Product with skucode " + dto.getProductSkuCode() + " is not exists."));
        return productBuilder.buildProductCardDto(product, dto.getColorHex());
    }

    @Override
    public List<ProductDto> getProductsByCollectionExcludeSkuCode(String collectionId, String skuCodeToExclude) {
        List<Product> products = productRepository
            .findAllByStatusNotDeletedAndCollectionIdExcludeSkuCode(new ObjectId(collectionId), skuCodeToExclude);
        return productBuilder.buildProductDtoList(products, isMain);
    }

    @Override
    public List<ProductForBasketDto> getProductsForBasket(List<ProductColorDto> productColorDtos) {
        Map<String, Product> productMap = new HashMap<>();
        Map<ProductColorDto, CheckingProductAvailableAndStatusDto> productAvailableAndStatusMap = new HashMap<>();

        List<String> skuCodes = productColorDtos.stream().map(ProductColorDto::getProductSkuCode).toList();
        List<Product> products = productRepository.findAllByStatusNotDeletedAndSkuCodeIn(skuCodes);
        Map<ProductColorDto, ImageProduct> imagesMap = imageRepositoryCustom.findImagesByMainPhotoTrueAndProductSkuCodeWithColorHexIn(productColorDtos);
        List<InventoryForBasketDto> inventoryForBasket = inventoryService.getProductAvailableStatus(productColorDtos);

        products.forEach(product -> productMap.put(product.getSkuCode(), product));
        inventoryForBasket.forEach(inventory -> productAvailableAndStatusMap.put(inventory.getProductColorDto(), inventory.getCheckingProductAvailableAndStatusDto()));

        return productBuilder.buildProductsShopCard(productMap, imagesMap, productColorDtos, productAvailableAndStatusMap);
    }

    private Pageable buildPageable(PageableDto pageable, SortDto sortDto) {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(DIRECTION_FOR_DEFAULT_SORTING, FIELD_NAME_FOR_DEFAULT_SORTING));

        if (sortDto.getFieldName() != null && sortDto.getDirection() != null) {
            Direction dir = sortDto.getDirection().equals(DIRECTION_ASC) ? Direction.ASC : Direction.DESC;
            orders.add(new Order(dir, sortDto.getFieldName()));
        } else {
            orders.add(new Order(DIRECTION_FOR_DEFAULT_SORTING, FIELD_NAME_FOR_ADDITIONAL_SORTING ));
        }
        return PageRequest.of(pageable.getPage(), pageable.getSize(), Sort.by(orders));
    }
}
