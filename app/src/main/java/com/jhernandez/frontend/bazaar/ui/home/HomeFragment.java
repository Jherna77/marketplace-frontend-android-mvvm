package com.jhernandez.frontend.bazaar.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentHomeBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.home.adapter.CategorySectionAdapter;
import com.jhernandez.frontend.bazaar.ui.home.adapter.HomeProductAdapter;
import com.jhernandez.frontend.bazaar.ui.product.detail.ProductDetailActivity;
import com.jhernandez.frontend.bazaar.ui.user.account.AccountActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment representing the home screen of the Bazaar app.
 * It displays various product sections and categories to the user.
 */
@NoArgsConstructor
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeProductAdapter favouriteProductsAdapter;
    private HomeProductAdapter recentProductsAdapter;
    private HomeProductAdapter topSellingProductsAdapter;
    private HomeProductAdapter topRatingProductsAdapter;
    private HomeProductAdapter discountedProductsAdapter;
    private CategorySectionAdapter categorySectionAdapter;
    private Context context;
    private LifecycleOwner owner;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initProgressDialog();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(HomeViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState();
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(context);
    }

    private void initAdapters() {
        favouriteProductsAdapter = new HomeProductAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvFavouriteProducts.setAdapter(favouriteProductsAdapter);

        recentProductsAdapter = new HomeProductAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvRecentProducts.setAdapter(recentProductsAdapter);

        topSellingProductsAdapter = new HomeProductAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvTopSellingProducts.setAdapter(topSellingProductsAdapter);

        topRatingProductsAdapter = new HomeProductAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvTopRatingProducts.setAdapter(topRatingProductsAdapter);

        discountedProductsAdapter = new HomeProductAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvDiscountedProducts.setAdapter(discountedProductsAdapter);

        categorySectionAdapter = new CategorySectionAdapter(new ArrayList<>(),
                viewModel::onProductSelected);
        binding.rvRandomCategories.setAdapter(categorySectionAdapter);
    }

    private void initListeners() {
        binding.llWelcome.setOnClickListener(v -> viewModel.onAccountSelected());
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getUser().observe(owner, this::setUserInfo);
        viewModel.getDiscountedProducts().observe(owner, discountedProductsAdapter::updateProducts);
        viewModel.getRecentProducts().observe(owner, recentProductsAdapter::updateProducts);
        viewModel.getTopSellingProducts().observe(owner, topSellingProductsAdapter::updateProducts);
        viewModel.getTopRatedProducts().observe(owner, topRatingProductsAdapter::updateProducts);
        viewModel.getFavouriteProducts().observe(owner, favouriteProductsAdapter::updateProducts);
        viewModel.getHomeCategories().observe(owner, categorySectionAdapter::updateCategories);
        viewModel.goToProductDetailEvent().observe(owner, this::goToProductDetail);
        viewModel.goToAccountEvent().observe(owner, go -> goToAccount());
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(HomeViewState state) {
        if (state.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
        binding.llWelcome.setVisibility(state.isAuthenticated() ? View.VISIBLE : View.GONE);
        binding.tvFavouriteProducts.setVisibility(state.isAuthenticated() && state.hasFavourites() ? View.VISIBLE : View.GONE);
        binding.rvFavouriteProducts.setVisibility(state.isAuthenticated() && state.hasFavourites() ? View.VISIBLE : View.GONE);
    }

    private void setUserInfo(User user) {
        binding.tvWelcome.setText(String.format(getString(R.string.welcome), user.name()));
        binding.tvAddress.setText(user.address());
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToAccount() {
        startActivity(AccountActivity.create(context));
    }

    private void goToProductDetail(Long productId) {
        startActivity(ProductDetailActivity.create(context, productId));
    }

}
