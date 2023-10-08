package com.cozyhome.onlineshop.reviewservice.service;

import com.cozyhome.onlineshop.dto.review.ReviewResponse;
import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;
import com.cozyhome.onlineshop.userservice.model.Role;

import java.util.List;
import java.util.Set;

public interface ReviewService {
    List<ReviewAdminResponse> getReviews();

    ReviewResponse addNewReview(ReviewRequest review, String userId);

    List<ReviewResponse> getReviewsForProduct(String productSkuCode);

    void removeReviewById(String reviewId, String userId, Set<Role> roles);

    List<ReviewAdminResponse> getReviewsForProductAllInf(String productSkuCode);
}
