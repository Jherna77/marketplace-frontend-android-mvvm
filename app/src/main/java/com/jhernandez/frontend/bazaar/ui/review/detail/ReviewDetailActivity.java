package com.jhernandez.frontend.bazaar.ui.review.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_REVIEW;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityReviewDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.Review;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

/*
 * Activity for displaying the details of a review.
 * It handles displaying review and product information, and navigation back.
 */
public class ReviewDetailActivity extends AppCompatActivity {

    private ActivityReviewDetailBinding binding;
    private ReviewDetailViewModel viewModel;
    private Long review_id;

    public static Intent create(Context context, Long id) {
        return new Intent(context, ReviewDetailActivity.class).putExtra(ARG_REVIEW, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        review_id = getIntent().getLongExtra(ARG_REVIEW, NO_ARG);
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.product_review);
        initViewModel();
        initListeners();
        initObservers();
        viewModel.findReviewById(review_id);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ReviewDetailViewModel.class);
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
        viewModel.getReview().observe(this, this::setReviewInfo);
        viewModel.getProduct().observe(this, this::setProductInfo);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void setReviewInfo(Review review) {
        binding.tvReviewAuthor.setText(review.author());
        binding.ratingBar.setRating(review.rating());
        binding.tvComment.setText(review.comment());
        binding.tvReviewDate.setText(String.format(getString(R.string.string_format), review.reviewDate()));
    }

    private void setProductInfo(Product product) {
        binding.tvProductName.setText(product.name());
        binding.tvProductDescription.setText(product.description());
        ViewUtils.showImageOnImageView(this, product.imagesUrl().get(0), binding.productImage);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goBack() {
        finish();
    }
}