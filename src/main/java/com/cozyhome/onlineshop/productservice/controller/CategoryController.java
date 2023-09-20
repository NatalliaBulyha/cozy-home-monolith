package com.cozyhome.onlineshop.productservice.controller;

import com.cozyhome.onlineshop.productservice.controller.swagger.CommonApiResponses;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.dto.CategoryWithIconDto;
import com.cozyhome.onlineshop.dto.CategoryWithPhotoDto;
import com.cozyhome.onlineshop.dto.CategoryWithSubCategoriesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
    "${api.front.additional_url}" })
@Tag(name = "Category")
@CommonApiResponses
public interface CategoryController {
    @Operation(summary = "Fetch all categories", description = "Fetch all categories with id, name, spriteIcon")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    ResponseEntity<List<CategoryWithIconDto>> getCategoryWithIcon();

    @Operation(summary = "Fetch all categories with subcategories for catalog", description = "Fetch all categories with subcategories and images for categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    ResponseEntity<List<CategoryWithSubCategoriesDto>> getCategoriesWithSubcategoriesAndPhoto();

    @Operation(summary = "Fetch all categories with photos for homepage", description = "Fetch all categories with photos for homepage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    ResponseEntity<List<CategoryWithPhotoDto>> getCategoriesWithPhoto();
}
