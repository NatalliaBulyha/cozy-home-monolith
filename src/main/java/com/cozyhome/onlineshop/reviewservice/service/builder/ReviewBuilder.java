package com.cozyhome.onlineshop.reviewservice.service.builder;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.reviewservice.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewBuilder {
    public List<ReviewDto> buildReviewsResponse(List<Review> reviews) {
        return reviews.stream().map(this::buildReviewResponse).toList();
    }

    public ReviewDto buildReviewResponse(Review review) {
            return ReviewDto.builder()
                    .rating((byte) review.getRating())
                    .userName(review.getUserName())
                    .review(review.getComment())
                    .data(review.getModifiedAt().toLocalDate())
                    .build();
    }
}
