package com.cozyhome.onlineshop.reviewservice.service;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;

import java.util.List;

public interface ReviewService {
    List<ReviewAdminResponse> getReviews();

    ReviewDto addNewReview(ReviewRequest review, String userId);

    List<ReviewDto> getReviewsForProduct(String productSkuCode);

    void removeReviewById(String reviewId, String userId);

    List<ReviewAdminResponse> getReviewsForProductAllInf(String productSkuCode);
}
