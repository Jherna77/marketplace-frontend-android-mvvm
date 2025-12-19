package com.jhernandez.frontend.bazaar.ui.review.product;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityProductReviewsBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.ArrayList;

/*
 * Activity for displaying the reviews of a product.
 * It handles displaying the list of reviews, navigation back, and error handling.
 */
public class ProductReviewsActivity extends AppCompatActivity {

    private ActivityProductReviewsBinding binding;
    private ProductReviewsViewModel viewModel;
    private ProductReviewsAdapter adapter;
    private Long productId;

    public static Intent create(Context context, Long id) {
        return new Intent(context, ProductReviewsActivity.class).putExtra(ARG_PRODUCT, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productId = getIntent().getLongExtra(ARG_PRODUCT, NO_ARG);
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.product_reviews);
        initViewModel();
        initAdapters();
        initListeners();
        initObservers();
        viewModel.findReviewsByProductId(productId);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ProductReviewsViewModel.class);
    }

    private void initAdapters() {
        adapter = new ProductReviewsAdapter(new ArrayList<>());
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
        viewModel.getReviews().observe(this, adapter::updateReviews);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goBack() {
        finish();
    }
}