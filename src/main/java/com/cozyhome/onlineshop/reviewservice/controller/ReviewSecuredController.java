package com.cozyhome.onlineshop.reviewservice.controller;

import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRemoveDto;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;
import com.cozyhome.onlineshop.dto.review.ReviewResponse;
import com.cozyhome.onlineshop.productservice.controller.swagger.CommonApiResponses;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.reviewservice.service.ReviewService;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.validation.ValidSkuCode;
import com.cozyhome.onlineshop.validation.ValidUUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Validated
@CommonApiResponses
@Tag(name = "Review")
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.secure.basePath}/review")
public class ReviewSecuredController {

    private final ReviewService reviewService;
    @Value("${header.name.user-id}")
    private String userIdName;
    @Value("${header.name.user-role}")
    private String userRoleAttributeName;

    @Operation(summary = "Fetch all reviews. For the ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @GetMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<ReviewAdminResponse>> getAllReviewsAllInfo() {
        return new ResponseEntity<>(reviewService.getReviews(), HttpStatus.OK);
    }

    @Operation(summary = "Add new review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_201_CREATED_DESCRIPTION) })
    @PostMapping("/new")
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    public ResponseEntity<ReviewResponse> addNewReview(@RequestBody @Valid ReviewRequest review,
                                                       HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdName);
        return new ResponseEntity<>(reviewService.addNewReview(review, userId), HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch information about review for the product. For the ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
    @GetMapping("/admin/product")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<ReviewAdminResponse>> getReviewsForProductAllInf(@RequestParam @ValidSkuCode String productSkuCode) {
        return new ResponseEntity<>(reviewService.getReviewsForProductAllInf(productSkuCode), HttpStatus.OK);
    }

    @Operation(summary = "Remove review for the product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_DELETED_DESCRIPTION) })
    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
    public ResponseEntity<Void> removeReviewById(@RequestParam @ValidUUID String reviewId,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute(userIdName);
        Set<Role> roles = (Set<Role>) request.getAttribute(userRoleAttributeName);
        reviewService.removeReviewById(new ReviewRemoveDto(reviewId, userId, roles));
        return new ResponseEntity<>(HttpStatus.OK);
    }
 }
