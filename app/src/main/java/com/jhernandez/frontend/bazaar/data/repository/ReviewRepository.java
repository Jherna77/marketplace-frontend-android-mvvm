package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.ReviewMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing review-related operations.
 */
@RequiredArgsConstructor
public class ReviewRepository implements ReviewRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createReview(Review review, SuccessCallback callback) {
        apiService.createReview(ReviewMapper.toDto(review))
                .enqueue(CallbackDelegator.delegate("createReview", callback));
    }

    @Override
    public void findReviewById(Long id, TypeCallback<Review> callback) {
        apiService.findReviewById(id)
                .enqueue(CallbackDelegator.delegate(
                        "findReviewById",
                        response ->
                                callback.onSuccess(ReviewMapper.toDomain(response)),
                                callback::onError
                ));
    }

    @Override
    public void findReviewsByProductId(Long productId, TypeCallback<List<Review>> callback) {
        apiService.findReviewsByProductId(productId)
                .enqueue(CallbackDelegator.delegate(
                        "findReviewsByProductId",
                        response ->
                                callback.onSuccess(ReviewMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findReviewsByUserId(Long userId, TypeCallback<List<Review>> callback) {
        apiService.findReviewsByUserId(userId)
                .enqueue(CallbackDelegator.delegate(
                        "findReviewsByUserId",
                        response ->
                                callback.onSuccess(ReviewMapper.toDomainList(response)),
                                callback::onError));
    }
}
