package com.jhernandez.frontend.bazaar.ui.review.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_ORDER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityManageReviewBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

/*
 * Activity for managing reviews.
 * It handles adding or editing a review for a specific order.
 */
public class ManageReviewActivity extends AppCompatActivity {

    private ActivityManageReviewBinding binding;
    private ManageReviewViewModel viewModel;
    private Long orderId;
    private AlertDialog progressDialog;

    public static Intent create(Context context, Long id) {
        return new Intent(context, ManageReviewActivity.class).putExtra(ARG_ORDER, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        orderId = getIntent().getLongExtra(ARG_ORDER, NO_ARG);
        updateUI();
    }

    private void updateUI() {
        initViewModel();
        initViewState();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ManageReviewViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState(orderId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.btnAddReview.setOnClickListener(v -> addReview());
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                viewModel.onRatingSelected((int) rating);
            }
        });
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);
        viewModel.getProduct().observe(this, this::setProductInfo);
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getValidationError().observe(this, this::onValidationError);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(ManageReviewViewState viewState) {
        binding.header.title.setText(R.string.add_review);
        if (viewState.isLoading()) { progressDialog.show(); } else { progressDialog.dismiss(); }
    }

    private void setProductInfo(Product product) {
        binding.tvProductName.setText(product.name());
        binding.tvProductDescription.setText(product.description());
        ViewUtils.showImageOnImageView(this, product.imagesUrl().get(0), binding.productImage);
    }

    private void addReview() {
        viewModel.validateFields(binding.etComment.getText().toString().trim());
    }

    private void onSuccess(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onValidationError(ValidationError error) {
        if (error != null ) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    private void onApiError(ApiErrorResponse error) {
        if (error != null) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    private void goBack() {
        finish();
    }
}