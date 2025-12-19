package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Review;

import java.util.List;

/*
 * Interface representing the ReviewRepositoryPort.
 */
public interface ReviewRepositoryPort {

    void createReview(Review review, SuccessCallback callback);
    void findReviewById(Long id, TypeCallback<Review> callback);
    void findReviewsByProductId(Long productId, TypeCallback<List<Review>> callback);
    void findReviewsByUserId(Long userId, TypeCallback<List<Review>> callback);
}
