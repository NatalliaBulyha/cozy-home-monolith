package com.cozyhome.onlineshop.userservice.controller;

import com.cozyhome.onlineshop.dto.user.AddressIdDto;
import com.cozyhome.onlineshop.dto.user.AddressRequest;
import com.cozyhome.onlineshop.dto.user.AddressResponse;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.userservice.security.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = { "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
		"${api.front.additional_url}", "${api.front.main.url}" }, allowedHeaders = { "Authorization" },
	    exposedHeaders = { "Access-Control-Allow-Methods" })
@Tag(name = "User")
@ApiResponse
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${api.secure.basePath}/address")
public class AddressSecuredController {
    @Value("${header.name.user-id}")
    private String userIdAttribute;
    private final AddressService addressService;

    @Operation(summary = "New address", description = "The user adds new address to his personal account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER"})
    @PostMapping("/new")
    public ResponseEntity<AddressResponse> saveAddress(@RequestBody @Valid AddressRequest addressRequest,
                                                       HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdAttribute);
        return ResponseEntity.ok(addressService.saveAddress(addressRequest, userId));
    }

    @Operation(summary = "Delete address.", description = "The user deletes the address in his personal account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @PostMapping
    public ResponseEntity<Void> deleteAddress(@RequestBody @Valid AddressIdDto addressId, HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdAttribute);
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get addresses.", description = "Get all addresses for user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses(HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdAttribute);
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }
}
