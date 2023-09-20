package com.cozyhome.onlineshop.reviewservice.controller;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;
import com.cozyhome.onlineshop.reviewservice.service.ReviewService;
import com.cozyhome.onlineshop.validation.ValidSkuCode;
import com.cozyhome.onlineshop.validation.ValidUUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/review")
public class ReviewControllerImpl {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews() {
        return new ResponseEntity<>(reviewService.getReviews(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<ReviewDto> addNewReview(@RequestBody @Valid ReviewRequest review) {
        return new ResponseEntity<>(reviewService.addNewReview(review), HttpStatus.CREATED);
    }

    @GetMapping("/product")
    public ResponseEntity<List<ReviewDto>> getReviewsForProduct(@RequestParam @ValidSkuCode String productSkuCode) {
        return new ResponseEntity<>(reviewService.getReviewsForProduct(productSkuCode), HttpStatus.OK);
    }

    @GetMapping("/admin/product")
    public ResponseEntity<List<ReviewAdminResponse>> getReviewsForProductAllInf(@RequestParam @ValidSkuCode String productSkuCode) {
        return new ResponseEntity<>(reviewService.getReviewsForProductAllInf(productSkuCode), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> removeReviewById(@RequestParam @ValidUUID String reviewId) {
        reviewService.removeReviewById(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
 }
