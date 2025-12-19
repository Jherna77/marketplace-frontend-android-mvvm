package com.jhernandez.frontend.bazaar.ui.review.user;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityUserReviewsBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListCheckAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.review.detail.ReviewDetailActivity;

import java.util.ArrayList;

/*
 * Activity for displaying the reviews of a user.
 * It handles displaying the list of reviews, navigation back, and error handling.
 */
public class UserReviewsActivity extends AppCompatActivity {

    private ActivityUserReviewsBinding binding;
    private UserReviewsViewModel viewModel;
    private ItemListCheckAdapter<Review> adapter;

    public static Intent create(Context context) {
        return new Intent(context, UserReviewsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(getString(R.string.reviews));
        initViewModel();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(UserReviewsViewModel.class);
    }

    private void initAdapters() {
        adapter = new ItemListCheckAdapter<>(
                new ArrayList<>(),
                new ItemListCheckAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Review review) {
                        return review.reviewDate() + " - " + review.rating().toString();
                    }

                    @Override
                    public void onCheckClicked(Review review) {
                        viewModel.onCheckReviewSelected(review.id());
                    }
                }
        );
        binding.rvReviews.setAdapter(adapter);
    }

    private void initListeners() {
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getReviews().observe(this, adapter::updateItems);
        viewModel.goToReviewDetailEvent().observe(this, this::goToReviewDetail);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goToReviewDetail(Long reviewId) {
        startActivity(ReviewDetailActivity.create(this, reviewId));
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }

}