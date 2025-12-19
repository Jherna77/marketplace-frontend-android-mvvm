package com.jhernandez.frontend.bazaar.ui.review.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for displaying the reviews of a user.
 * It manages the data and interactions related to user reviews.
 */
public class UserReviewsViewModel extends BaseViewModel {

    private final ReviewRepositoryPort reviewRepository;
    private final MutableLiveData<List<Review>> reviews = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToReviewDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }
    public LiveData<Long> goToReviewDetailEvent() {
        return _goToReviewDetail;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public UserReviewsViewModel(ReviewRepositoryPort reviewRepository, SessionRepositoryPort sessionRepository) {
        this.reviewRepository = reviewRepository;
        findReviewsByUserId(sessionRepository.getSessionUser().id());
    }

    public void findReviewsByUserId(Long userId) {
        Log.d("UserReviewsViewModel", "Loading reviews for user with id " + userId);
        reviewRepository.findReviewsByUserId(userId, typeCallback(reviews));
    }

    public void onCheckReviewSelected(Long orderId) {
        _goToReviewDetail.setValue(orderId);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}