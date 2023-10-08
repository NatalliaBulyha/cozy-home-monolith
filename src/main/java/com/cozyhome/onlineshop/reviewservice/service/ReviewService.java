package com.cozyhome.onlineshop.reviewservice.service;

import com.cozyhome.onlineshop.dto.review.ReviewResponse;
import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;

import java.util.List;

public interface ReviewService {
    List<ReviewAdminResponse> getReviews();

    ReviewResponse addNewReview(ReviewRequest review, String userId);

    List<ReviewResponse> getReviewsForProduct(String productSkuCode);

    void removeReviewById(String reviewId, String userId);

    List<ReviewAdminResponse> getReviewsForProductAllInf(String productSkuCode);
}
