package com.jhernandez.frontend.bazaar.ui.product.shop;

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
import com.jhernandez.frontend.bazaar.databinding.FragmentShopProductsBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.product.adapter.ShopProductAdapter;
import com.jhernandez.frontend.bazaar.ui.product.manage.ManageProductActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for displaying the products of a shop.
 * It handles displaying the list of products, navigation to add/edit products, and showing loading and error states.
 */
@NoArgsConstructor
public class ShopProductsFragment extends Fragment {

    private FragmentShopProductsBinding binding;
    private ShopProductsViewModel viewModel;
    private ShopProductAdapter adapter;
    private Context context;
    private LifecycleOwner owner;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShopProductsBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initAdapters();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(ShopProductsViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState();
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(context);
    }

    private void initAdapters() {
        adapter = new ShopProductAdapter(new ArrayList<>(), viewModel::onEditProductSelected);
        binding.rvProducts.setAdapter(adapter);
    }

    private void initListeners() {
        binding.addFab.setOnClickListener(v -> viewModel.onAddProductSelected());
    }

    private void initObservers() {
        viewModel.getShop().observe(owner, this::setShopInfo);
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getProducts().observe(owner, adapter::updateProducts);
        viewModel.getGoToAddProductEvent().observe(owner, go -> goToAddProduct());
        viewModel.getGoToEditProductEvent().observe(owner, this::goToEditProduct);
        viewModel.getApiError().observe(owner, this::showError);
    }

    private void setShopInfo(User shop) {
        binding.tvShopName.setText(String.format(context.getString(R.string.shop_products), shop.name()));
    }

    private void updateUI(ShopProductsViewState viewState) {
        binding.tvInfo.setVisibility(!viewState.hasProducts() ? View.VISIBLE : View.GONE);
        if (viewState.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
    }

    private void showError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToAddProduct() {
        startActivity(ManageProductActivity.create(context));
        getActivity().finish();
    }

    private void goToEditProduct(Long productId) {
        startActivity(ManageProductActivity.create(context, productId));
        getActivity().finish();
    }

}