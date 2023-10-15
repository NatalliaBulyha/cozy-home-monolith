package com.cozyhome.onlineshop.shoppingcartservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartDto;
import com.cozyhome.onlineshop.dto.shoppingcart.ShoppingCartLineDto;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.shoppingcartservice.service.ShoppingCartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = { "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
		"${api.front.additional_url}", "${api.front.main.url}" }, allowedHeaders = {
				"Authorization" }, exposedHeaders = { "Access-Control-Allow-Methods" })
@Tag(name = "Shopping cart")
@ApiResponse
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("${api.secure.basePath}/shopping-cart")
public class ShoppingCartSecuredController {

	private final ShoppingCartService shoppingCartService;

	@Value("${header.name.user-id}")
	private String userIdAttribute;

	@Operation(summary = "Get user's shopping cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@Secured({ "ROLE_CUSTOMER" })
	@GetMapping()
	public ResponseEntity<List<ShoppingCartDto>> getShoppingCart(HttpServletRequest request) {
		String userId = (String) request.getAttribute(userIdAttribute);
		return ResponseEntity.ok(shoppingCartService.getShoppingCart(userId));
	}

	@Operation(summary = "Get user's shopping cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@Secured({ "ROLE_CUSTOMER" })
	@PostMapping()
	public ResponseEntity<List<ShoppingCartDto>> synchronizeShoppingCart(HttpServletRequest request,
			@Valid @RequestBody List<ShoppingCartLineDto> dtoList) {

		String userId = (String) request.getAttribute(userIdAttribute);
		shoppingCartService.synchronizeShoppingCart(userId, dtoList);
		return ResponseEntity.ok(shoppingCartService.getShoppingCart(userId));
	}
}
