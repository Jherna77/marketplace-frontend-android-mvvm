package com.jhernandez.frontend.bazaar.ui.product.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentAdminProductsBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListEditAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.product.manage.ManageProductActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for managing products in the admin dashboard.
 * It allows filtering, ordering, and editing of products.
 */
@NoArgsConstructor
public class AdminProductsFragment extends Fragment {

    private FragmentAdminProductsBinding binding;
    private AdminProductsViewModel viewModel;
    private ItemListEditAdapter<Product> adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminProductsBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(AdminProductsViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initAdapters() {
        adapter = new ItemListEditAdapter<>(
                new ArrayList<>(),
                new ItemListEditAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Product product) { return product.name(); }

                    @Override
                    public void onEditClicked(Product product) { viewModel.onEditProductSelected(product.id()); }
                }
        );
        binding.rvProducts.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAllStatuses.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipAllStatuses.getTag().toString()));
        binding.chipEnabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipEnabled.getTag().toString()));
        binding.chipDisabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipDisabled.getTag().toString()));
        binding.chipAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipAscending.getTag().toString()));
        binding.chipDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipDescending.getTag().toString()));
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getFilteredProducts().observe(owner, adapter::updateItems);
        viewModel.goToEditProductEvent().observe(owner, this::goToEditProduct);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(AdminProductsViewState viewState) {
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToEditProduct(Long productId) {
        startActivity(ManageProductActivity.create(context, productId));
        getActivity().finish();
    }

}