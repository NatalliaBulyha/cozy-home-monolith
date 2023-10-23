package com.cozyhome.onlineshop.reviewservice.service.builder;

import com.cozyhome.onlineshop.dto.review.ReviewResponse;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.reviewservice.model.Review;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewBuilder {
    private final UserRepository userRepository;
    public List<ReviewResponse> buildReviewsResponse(List<Review> reviews) {
        return reviews.stream().map(this::buildReviewResponse).toList();
    }

    public ReviewResponse buildReviewResponse(Review review) {
        String userId = review.getUserId();
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s doesn't found", userId)));
        log.info("[ON getReviewsForProductAllInf]:: build review with id: {} for user: {}.", review.getId(), review.getUserId());
        return ReviewResponse.builder()
                    .reviewId(review.getId().toString())
                    .rating((byte) review.getRating())
                    .userName(user.getFirstName() + " " + user.getLastName())
                    .review(review.getComment())
                    .data(review.getModifiedAt().toLocalDate())
                    .build();
    }
}
