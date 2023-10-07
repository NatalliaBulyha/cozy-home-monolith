package com.cozyhome.onlineshop.reviewservice.service;

import com.cozyhome.onlineshop.dto.review.ReviewDto;
import com.cozyhome.onlineshop.dto.review.ReviewAdminResponse;
import com.cozyhome.onlineshop.dto.review.ReviewRequest;
import com.cozyhome.onlineshop.exception.DataNotExistException;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.reviewservice.model.Review;
import com.cozyhome.onlineshop.reviewservice.repository.ReviewRepository;
import com.cozyhome.onlineshop.reviewservice.service.builder.ReviewBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository repository;
    private final ModelMapper mapper;
    private final ReviewBuilder reviewBuilder;
    @Override
    public List<ReviewDto> getReviews() {
        List<Review> reviews = repository.findAll();
        if (reviews.isEmpty()) {
            throw new DataNotFoundException("The are no reviews.");
        }
        return reviews.stream().map(review -> mapper.map(review, ReviewDto.class)).toList();
    }

    @Override
    public ReviewDto addNewReview(ReviewRequest reviewRequest, String userId) {
        Review review = mapper.map(reviewRequest, Review.class);
        review.setUserId(userId);
        review.setCreatedAt(LocalDateTime.now());
        review.setModifiedAt(LocalDateTime.now());
        Review savedReview = repository.save(review);
        return reviewBuilder.buildReviewResponse(savedReview);
    }

    @Override
    public List<ReviewDto> getReviewsForProduct(String productSkuCode) {
        List<Review> reviews = repository.findReviewsByProductSkuCode(productSkuCode);
        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }
        return reviewBuilder.buildReviewsResponse(reviews);
    }

    @Override
    public void removeReviewById(String reviewId) {
        boolean exist = repository.existsById(UUID.fromString(reviewId));
        if (exist) {
            repository.deleteById(UUID.fromString(reviewId));
        } else {
            throw new DataNotExistException("Review with id = " + reviewId + " isn't exist.");
        }
    }

    @Override
    public List<ReviewAdminResponse> getReviewsForProductAllInf(String productSkuCode) {
        List<Review> reviews = repository.findReviewsByProductSkuCode(productSkuCode);
        if (reviews.isEmpty()) {
            throw new DataNotExistException("Review for product with sku code = " + productSkuCode + " isn't exist.");
        }
        return reviews.stream().map(review -> mapper.map(review, ReviewAdminResponse.class)).toList();
    }
}
