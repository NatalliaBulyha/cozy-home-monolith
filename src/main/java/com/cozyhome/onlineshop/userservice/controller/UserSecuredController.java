package com.cozyhome.onlineshop.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozyhome.onlineshop.dto.FavoriteProductDto;
import com.cozyhome.onlineshop.dto.request.PageableDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.dto.user.UserInformationResponse;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenUtil;
import com.cozyhome.onlineshop.userservice.security.service.FavoriteProductsService;
import com.cozyhome.onlineshop.userservice.security.service.TokenBlackListService;
import com.cozyhome.onlineshop.userservice.security.service.UserService;
import com.cozyhome.onlineshop.validation.ValidId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = { "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
		"${api.front.additional_url}", "${api.front.main.url}" }, allowedHeaders = { "Authorization" },
	    exposedHeaders = { "Access-Control-Allow-Methods" })
@Tag(name = "User")
@ApiResponse
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${api.secure.basePath}/user")
public class UserSecuredController {
    @Value("${header.name.user-id}")
    private String userIdAttribute;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;
    private final JwtTokenUtil jwtTokenUtil;
    private final FavoriteProductsService favoriteProductService;

    @Operation(summary = "Update user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @PutMapping("/profile/update")
    public ResponseEntity<UserInformationResponse> updateUserInfo(@RequestBody @Valid UserInformationRequest userInformationDto,
                                                                  HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdAttribute);
        return ResponseEntity.ok(userService.updateUserData(userInformationDto, userId));
    }

    @Operation(summary = "Get user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @GetMapping("/profile")
    public ResponseEntity<UserInformationResponse> getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdAttribute);
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @Operation(summary = "Logout.", description = "Logout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String jwtToken = jwtTokenUtil.resolveToken(request);
        tokenBlackListService.saveTokenToBlackList(jwtToken);
        log.warn("[ON logout] :: JwtToken {} was added to Black List.", jwtToken);
        return ResponseEntity.ok().build();
    } 
    
    @Operation(summary = "Add product to favorite.", description = "Add product to favorite by product skuCode and color hex.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @PostMapping("/favorite-items/update")
    public ResponseEntity<Void> updateUserFavoriteProducts(HttpServletRequest request, @Valid @RequestBody ProductColorDto dtoRequest) {
    	String userId = (String) request.getAttribute(userIdAttribute);        
        favoriteProductService.updateUserFavoriteProducts(userId, dtoRequest);
        return ResponseEntity.ok().build();
    } 
    
    @Operation(summary = "Get all of the user's favorite products.", description = "Get all of the user's favorite products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @GetMapping("/favorite-products")
    public ResponseEntity<List<FavoriteProductDto>> getFavoriteProductsForUserId(HttpServletRequest request, PageableDto pageable) {
    	String userId = (String) request.getAttribute(userIdAttribute);
        log.info("[ON getFavoriteProductsForUser] :: Get all favorite products for user with id {}", userId);
        return ResponseEntity.ok(favoriteProductService.getFavoriteProductsByUserId(userId, pageable));
    } 
    
    @Operation(summary = "Get user's favorite products by category id.", description = "Get user's favorite products by category id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @GetMapping("/favorite-products/category-id")
    public ResponseEntity<List<FavoriteProductDto>> getFavoriteProductsForUserAndCategoryId(HttpServletRequest request, @RequestParam @ValidId String categoryId,
    		PageableDto pageable) {
    	String userId = (String) request.getAttribute(userIdAttribute);
        log.info("[ON getFavoriteProductsForUserAndCategoryId] :: Get all favorite items for user with id {} and category id {}.", userId, categoryId);
        return ResponseEntity.ok(favoriteProductService.getFavoriteProductsByUserIdAndCategoryId(userId, categoryId, pageable));
    }
}
