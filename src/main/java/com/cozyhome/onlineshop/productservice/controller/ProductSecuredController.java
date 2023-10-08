package com.cozyhome.onlineshop.productservice.controller;

import com.cozyhome.onlineshop.dto.ProductForBasketDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
        "${api.front.additional_url}", "${api.front.main.url}" })
@Tag(name = "Product")
@ApiResponse
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${api.secure.basePath}/product")
public class ProductSecuredController {

	private final ProductService productService;

    @Operation(summary = "Get products information for basket by product sku code and color hex", description = "Get list of products information for basket by product sku code and color hex.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION),
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_400, description = SwaggerResponse.Message.CODE_400) })
    @PostMapping("/basket")
    @Secured({"ROLE_CUSTOMER"})
    public ResponseEntity<List<ProductForBasketDto>> getProductsForBasket(@RequestBody @Valid List<ProductColorDto> productColorDtos){
        return ResponseEntity.ok().body(productService.getProductsForBasket(productColorDtos));
    }
}
