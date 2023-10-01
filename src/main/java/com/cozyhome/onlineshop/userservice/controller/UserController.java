package com.cozyhome.onlineshop.userservice.controller;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;
import com.cozyhome.onlineshop.dto.user.NewUserDto;
import com.cozyhome.onlineshop.dto.user.UserDto;
import com.cozyhome.onlineshop.productservice.controller.swagger.CommonApiResponses;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@CommonApiResponses
@Tag(name = "User")
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Add new review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_201_CREATED_DESCRIPTION) })
    @PostMapping("/new")
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid NewUserDto user) {
        return new ResponseEntity<>(userService.addNewUser(user), HttpStatus.CREATED);
    }
}
