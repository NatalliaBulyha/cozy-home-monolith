package com.cozyhome.onlineshop.reviewservice.service.builder;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.reviewservice.model.Review;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewBuilder {
    private final UserRepository userRepository;
    public List<ReviewDto> buildReviewsResponse(List<Review> reviews) {
        return reviews.stream().map(this::buildReviewResponse).toList();
    }

    public ReviewDto buildReviewResponse(Review review) {
        String userId = review.getUserId();
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s doesn't found", userId)));
            return ReviewDto.builder()
                    .rating((byte) review.getRating())
                    .userName(user.getId())
                    .review(review.getComment())
                    .data(review.getModifiedAt().toLocalDate())
                    .build();
    }
}
