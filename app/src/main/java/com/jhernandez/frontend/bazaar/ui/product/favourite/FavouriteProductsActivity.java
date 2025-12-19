package com.jhernandez.frontend.bazaar.ui.product.favourite;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityFavouriteProductsBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.product.adapter.ResultProductAdapter;
import com.jhernandez.frontend.bazaar.ui.product.detail.ProductDetailActivity;

import java.util.ArrayList;

/*
 * Activity for displaying the user's favorite products.
 * It handles displaying the list, navigation to product details, and error handling.
 */
public class FavouriteProductsActivity extends AppCompatActivity {

    private ActivityFavouriteProductsBinding binding;
    private FavouriteProductsViewModel viewModel;
    private ResultProductAdapter adapter;

    public static Intent create(Context context) {
        return new Intent(context, FavouriteProductsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.favourite_products);
        initViewmodel();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewmodel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(FavouriteProductsViewModel.class);
    }

    private void initAdapters() {
        adapter = new ResultProductAdapter(new ArrayList<>(), viewModel::onProductSelected);
        binding.rvProducts.setAdapter(adapter);
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
        viewModel.getProducts().observe(this, adapter::updateProducts);
        viewModel.goToProductDetailEvent().observe(this, this::goToProductDetail);
        viewModel.showInfoTVEvent().observe(this, this::updateUI);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(Boolean show) {
        binding.tvInfo.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goToProductDetail(Long productId) {
        startActivity(ProductDetailActivity.create(this, productId));
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }

}