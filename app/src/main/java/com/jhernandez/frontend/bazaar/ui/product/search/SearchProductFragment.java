package com.jhernandez.frontend.bazaar.ui.product.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentSearchProductBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.ui.category.adapter.CategoryAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.product.adapter.ResultProductAdapter;
import com.jhernandez.frontend.bazaar.ui.product.detail.ProductDetailActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for searching products.
 * It handles displaying search results, filtering, and navigation to product details.
 */
@NoArgsConstructor
public class SearchProductFragment extends Fragment {

    private FragmentSearchProductBinding binding;
    private SearchProductViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private ResultProductAdapter productAdapter;
    private Context context;
    private LifecycleOwner owner;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false);
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
                .get(SearchProductViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(context);
    }

    private void initAdapters() {
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), viewModel::onCategorySelected);
        binding.rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ResultProductAdapter(new ArrayList<>(), viewModel::onProductSelected);
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAllStatuses.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipAllStatuses.getTag().toString()));
        binding.chipPromo.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipPromo.getTag().toString()));
        binding.chipPriceAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipPriceAscending.getTag().toString()));
        binding.chipPriceDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipPriceDescending.getTag().toString()));
        binding.chipRatingAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipRatingAscending.getTag().toString()));
        binding.chipRatingDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipRatingDescending.getTag().toString()));
        binding.tilSearchProduct.setEndIconOnClickListener(v -> findProductsByQuery());
        binding.etSearchProduct.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                findProductsByQuery();
                return true;
            }
            return false;
        });
    }

    private void initObservers() {
        viewModel.getCategories().observe(owner, categoryAdapter::updateCategories);
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.showResultsInfoEvent().observe(owner, this::showResultsInfo);
        viewModel.clearBoxEvent().observe(owner, this::clearSearchBox);
        viewModel.resetFilterEvent().observe(owner, this::resetFilter);
        viewModel.hideKeyboardEvent().observe(owner, this::hideKeyboard);
        viewModel.getProducts().observe(owner, productAdapter::updateProducts);
        viewModel.goToProductDetailEvent().observe(owner, this::goToProductDetail);
        viewModel.getValidationError().observe(owner, this::onValidationError);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(SearchProductViewState viewState) {
        binding.tvErrors.setText("");
        binding.tvInfo.setVisibility(!viewState.hasResults() ? View.VISIBLE : View.GONE);
        binding.rvProducts.setVisibility(viewState.hasResults() ? View.VISIBLE : View.GONE);
        binding.btnFilter.setEnabled(viewState.hasResults());
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
        if (viewState.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
    }

    private void findProductsByQuery() {
        viewModel.validateQuery(binding.etSearchProduct.getText().toString());
    }

    private void showResultsInfo(String searchTerm) {
        binding.btnFilter.setVisibility(View.VISIBLE);
        binding.tvResults.setText(String.format(getString(R.string.search_results), searchTerm));
    }

    private void clearSearchBox(Boolean clear) {
        if (clear) binding.etSearchProduct.setText("");
    }

    private void resetFilter(Boolean reset) {
        if (reset) {
            binding.chipAllStatuses.setChecked(true);
            binding.chipPriceAscending.setChecked(true);
        }
    }

    private void hideKeyboard(Boolean hide) {
        if (hide) ViewUtils.hideKeyboard(requireActivity());
    }

    private void onValidationError(ValidationError error) {
        ViewUtils.showErrorOnTextView(context, error, binding.tvErrors);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToProductDetail(Long productId) {
        startActivity(ProductDetailActivity.create(context, productId));
    }

}
