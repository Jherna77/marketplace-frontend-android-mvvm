package com.jhernandez.frontend.bazaar.ui.product.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CART;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityProductDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.auth.LoginActivity;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ImageSliderAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.review.product.ProductReviewsActivity;

import java.util.ArrayList;

/*
 * Activity for displaying detailed information about a product.
 * It handles user interactions such as adding to favorites, adding to cart, and viewing reviews.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ImageSliderAdapter adapter;
    private ProductDetailViewModel viewModel;
    private Long productId;

    public static Intent create(Context context, Long id) {
        return new Intent(context, ProductDetailActivity.class).putExtra(ARG_PRODUCT, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productId = getIntent().getLongExtra(ARG_PRODUCT, NO_ARG);
        initUI();
    }

    private void initUI() {
        initViewModel();
        initViewState();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewState() {
        viewModel.setViewState(productId);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ProductDetailViewModel.class);
    }

    private void initAdapters() {
        adapter = new ImageSliderAdapter(new ArrayList<>());
        binding.vpImage.setAdapter(adapter);
    }

    private void initListeners() {
        binding.llRating.setOnClickListener(v -> viewModel.onRatingSelected());
        binding.btnFav.setOnClickListener(v -> viewModel.onFavSelected());
        binding.btnAddToCart.setOnClickListener(v -> viewModel.onAddToCartSelected());
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
        viewModel.getShop().observe(this, this::setShopInfo);
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.goToLoginEvent().observe(this, goToLogin -> goToLogin());
        viewModel.goToProductReviewsEvent().observe(this, this::goToProductReviews);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(ProductDetailViewState viewState) {
        binding.header.title.setText(R.string.product_detail);
        binding.btnFav.setImageResource(viewState.isFavourite()
                ? R.drawable.ic_favourite
                : R.drawable.ic_no_favourite);
        binding.freeShipping.setVisibility(viewState.isFreeShipping() ? View.VISIBLE : View.GONE);
        binding.shipping.setVisibility(viewState.isFreeShipping() ? View.GONE : View.VISIBLE);
        binding.tvProductPricePaintFlags.setVisibility(viewState.hasDiscount() ? View.VISIBLE : View.GONE);
        binding.tvProductPricePaintFlags.setPaintFlags(
                binding.tvProductPricePaintFlags.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );
    }

    private void setProductInfo(Product product) {
        binding.productName.setText(product.name());
        binding.productDescription.setText(product.description());
        binding.productPrice.setText(String.format(
                getString(R.string.price_format),
                product.hasDiscount() ? product.discountPrice() : product.price()));
        binding.tvProductPricePaintFlags.setText(String.format(
                getString(R.string.price_format),
                product.price()));
        binding.shipping.setText(String.format(
                getString(R.string.shipping_tv),
                product.shipping()
        ));
        binding.tvStock.setText(String.format(
                getString(R.string.tv_stock),
                product.stock()));
        adapter.updateImages(product.imagesUrl());
        binding.ratingBar.setRating(product.rating().floatValue());
        binding.tvAvgRating.setText(String.format(
                getString(R.string.tv_average_rating),
                product.rating()
        ));
        binding.tvTotalRatings.setText(String.format(
                getString(R.string.tv_total_ratings),
                product.ratingCount()
        ));
        binding.tvSold.setText(String.format(
                getString(R.string.tv_sold),
                product.sold()));
    }

    private void setShopInfo(User shop) {
        binding.productShop.setText(String.format(getString(R.string.tv_product_shop), shop.name()));
    }

    private void onSuccess(Boolean success) {
        if (success) {
            ViewUtils.showToast(this, R.string.product_added_to_cart);
            startActivity(MainActivity.create(this, ARG_CART));
            finish();
        }
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorToast(this, error);
    }

    private void goToProductReviews(Long productId) {
        startActivity(ProductReviewsActivity.create(this, productId));
    }

    private void goToLogin() {
        startActivity(LoginActivity.create(this));
        finish();
    }

    private void goBack() {
        startActivity((MainActivity.create(this)));
        finish();
    }

}