package com.cozyhome.onlineshop.productservice.controller;

import com.cozyhome.onlineshop.productservice.controller.swagger.CommonApiResponses;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.dto.ImageDto;
import com.cozyhome.onlineshop.dto.PopUpImageDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardImageDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
    "${api.front.additional_url}" })
@Tag(name = "Image")
@CommonApiResponses
public interface ImageController {
    @Operation(summary = "Fetch image", description = "Fetch image by image's url")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400)})
    ResponseEntity<Resource> getImage(String imageName);

    @Operation(summary = "Fetch product's image by color for preview", description = "Fetch product's image with id, image path and color by product's skuCode and color's hex for preview")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400)})
    ResponseEntity<ImageDto> getPreviewImageForProductByColor(ProductColorDto productColor);

    @Operation(summary = "Fetch product's pop_up images by color", description = "Fetch product's pop_up image with id, image path")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400)})
    ResponseEntity<List<PopUpImageDto>> getPopUpImageForProductByColor(ProductColorDto productColor);

    @Operation(summary = "Fetch product card images by color", description = "Fetch images for product card by product's skuCode and color's hex")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400)})
    ResponseEntity<List<ProductCardImageDto>> getProductCardImagesByColor(ProductColorDto productColor);
}
