package com.jhernandez.frontend.bazaar.ui.review.manage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

/*
 * ViewModel for managing reviews.
 * It handles the logic for adding or editing a review for a specific order.
 */
public class ManageReviewViewModel extends BaseViewModel {

    private final ReviewRepositoryPort reviewRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final User author;
    private final MutableLiveData<ManageReviewViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();
    private Order order;
    private Integer rating;

    public LiveData<ManageReviewViewState> getViewState() {
        return viewState;
    }
    public LiveData<Product> getProduct() {
        return product;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }
    public LiveData<ValidationError> getValidationError() {
        return validationError;
    }

    public ManageReviewViewModel(ReviewRepositoryPort reviewRepository, OrderRepositoryPort orderRepository,
                                 ProductRepositoryPort productRepository, SessionRepositoryPort sessionRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.author = sessionRepository.getSessionUser();
    }

    public void setViewState(Long orderId) {
        viewState.setValue(new ManageReviewViewState(false));
        findOrderById(orderId);
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void findOrderById(Long id) {
        showLoading(true);
        Log.d("ManageReviewViewModel", "Loading order with id " + id);
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
                        showLoading(false);
                    }
                });
    }

    private void findProductById(Long id) {
        Log.d("ManageReviewViewModel", "Loading product with id " + id);
        productRepository.findProductById(id, typeCallback(product,
                productResult -> showLoading(false),
                error -> showLoading(false)));
    }

    private void createReview(Review review) {
        showLoading(true);
        Log.d("ManageReviewViewModel", "Creating review...");
        reviewRepository.createReview(review, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void validateFields(String comment) {
        if (comment.isEmpty()) { validationError.setValue(ValidationError.FIELD_EMPTY); }
        else if (rating == null) { validationError.setValue(ValidationError.RATING_EMPTY); }
        else {
            createReview(new Review(null, ViewUtils.nameFormatter(author), order.id(), comment, rating, null));
        }
    }

    public void onRatingSelected(Integer newRating) {
        this.rating = newRating;
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
