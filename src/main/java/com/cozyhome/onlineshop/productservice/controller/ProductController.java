package com.cozyhome.onlineshop.productservice.controller;

import com.cozyhome.onlineshop.productservice.controller.swagger.CommonApiResponses;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.dto.ProductDto;
import com.cozyhome.onlineshop.dto.ProductStatusDto;
import com.cozyhome.onlineshop.dto.ProductforBasket;
import com.cozyhome.onlineshop.dto.filter.FilterDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.request.SortDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
    "${api.front.additional_url}" })
@Tag(name = "Product")
@CommonApiResponses
public interface ProductController {
    @Operation(summary = "Fetch all statuses of product", description = "Retrieve all statuses of products.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    ResponseEntity<List<ProductStatusDto>> getProductsStatuses();

    @Operation(summary = "Fetch random products by status", description = "Fetch a specified number of random products based on the provided status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductDto>> getRandomProductsByStatus(Byte status, int countOfProducts);

    @Operation(summary = "Get Random Products by Status and Category ID", description = "Fetch a specified number of random products based on the provided status and category ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductDto>> getRandomProductsByStatusAndCategoryId(Byte status, String categoryId, int countOfProducts);

    @Operation(summary = "Get Products by Category ID", description = "Fetch products based on the provided category ID. Optionally, you can also provide a sub-category ID to filter the results.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductDto>> getProductsByCategoryId(String categoryId, PageableDto pageable);

    @Operation(summary = "Filter Products by Criterias", description = "Filter products based on the provided criterias. You can specify filtering options in the request body, along with pagination and sorting preferences.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductDto>> getFilteredProducts(FilterDto filter, PageableDto pageable, SortDto sortDto);

    @Operation(summary = "Get Filter Parameters", description = "Get the filter parameters for filtering products.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<FilterDto> getFilterParameters(FilterDto filter, byte size);

    @Operation(summary = "Get Product by skuCode and color's hex", description = "Get product by provided product's skuCode and color's hex.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<ProductCardDto> getProductCard(ProductColorDto dto);

    @Operation(summary = "Get Products by collection name excluding provided skuCode", description = "Get list of products by collection name excluding provided skuCode.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductDto>> getProductsByCollection(String collectionId, String productSkuCode);

    @Operation(summary = "Get products information for basket by product sku code and color hex", description = "Get list of products information for basket by product sku code and color hex.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    ResponseEntity<List<ProductforBasket>> getProductsForBasket(List<ProductColorDto> productColorDtos);
}
