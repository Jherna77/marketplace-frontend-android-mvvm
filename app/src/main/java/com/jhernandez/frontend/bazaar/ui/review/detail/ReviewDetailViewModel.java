package com.jhernandez.frontend.bazaar.ui.review.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for ReviewDetailActivity.
 * Manages the data and business logic for displaying review and product details.
 */
public class ReviewDetailViewModel extends BaseViewModel {

    private final ReviewRepositoryPort reviewRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final MutableLiveData<Review> review = new MutableLiveData<>();
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private Order order;

    public ReviewDetailViewModel(ReviewRepositoryPort reviewRepository, OrderRepositoryPort orderRepository,
                                 ProductRepositoryPort productRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public LiveData<Review> getReview() {
        return review;
    }
    public LiveData<Product> getProduct() {
        return product;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void findReviewById(Long id) {
        Log.d("ReviewDetailViewModel", "findReviewById: " + id);
        reviewRepository.findReviewById(id, typeCallback(
                review,
                reviewResult -> findOrderById(reviewResult.orderId()),
                error -> {}));
    }

    private void findOrderById(Long id) {
        Log.d("ReviewDetailViewModel", "Loading order with id " + id);
        orderRepository.findOrderById(id, new TypeCallback<>() {
            @Override
            public void onSuccess(Order orderResult) {
                order = orderResult;
                apiError.postValue(null);
                findProductById(order.item().getProduct().id());
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                order = null;
            }
        });
    }

    private void findProductById(Long id) {
        Log.d("ReviewDetailViewModel", "findProductById: " + id);
        productRepository.findProductById(id, typeCallback(product));
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }
}
