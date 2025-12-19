package com.jhernandez.frontend.bazaar.ui.review.product;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for managing product reviews data and interactions.
 * It fetches reviews from the repository and exposes them to the UI.
 */
public class ProductReviewsViewModel extends BaseViewModel {
    private final ReviewRepositoryPort reviewRepository;
    private final MutableLiveData<List<Review>> reviews = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public ProductReviewsViewModel(ReviewRepositoryPort reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void findReviewsByProductId(Long productId) {
        Log.d("ProductReviewsViewModel", "Loading reviews for product with id " + productId);
        reviewRepository.findReviewsByProductId(productId, typeCallback(reviews));
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}