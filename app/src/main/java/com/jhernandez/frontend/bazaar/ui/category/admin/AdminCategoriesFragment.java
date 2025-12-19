package com.jhernandez.frontend.bazaar.ui.category.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentAdminCategoriesBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.ui.category.manage.ManageCategoryActivity;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListEditAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for administering categories.
 * Allows filtering, adding, and editing categories through the UI.
 */
@NoArgsConstructor
public class AdminCategoriesFragment extends Fragment {

    private FragmentAdminCategoriesBinding binding;
    private AdminCategoriesViewModel viewModel;
    private ItemListEditAdapter<Category> adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCategoriesBinding.inflate(inflater, container, false);
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
                .get(AdminCategoriesViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initAdapters() {
        adapter = new ItemListEditAdapter<>(
                new ArrayList<>(),
                new ItemListEditAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Category category) {
                        return category.name();
                    }

                    @Override
                    public void onEditClicked(Category category) {
                        viewModel.onEditCategorySelected(category.id());
                    }
                }
        );
        binding.rvCategories.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAllStatuses.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipAllStatuses.getTag().toString()));
        binding.chipEnabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipEnabled.getTag().toString()));
        binding.chipDisabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipDisabled.getTag().toString()));
        binding.chipAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipAscending.getTag().toString()));
        binding.chipDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipDescending.getTag().toString()));
        binding.addFab.setOnClickListener(v -> viewModel.onAddCategorySelected());
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getFilteredCategories().observe(owner, adapter::updateItems);
        viewModel.goToAddCategoryEvent().observe(owner, go -> goToAddCategory());
        viewModel.goToEditCategoryEvent().observe(owner, this::goToEditCategory);
        viewModel.getApiError().observe(owner,this::onApiError);
    }

    private void updateUI(AdminCategoriesViewState viewState) {
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToAddCategory() {
        startActivity(ManageCategoryActivity.create(context));
        getActivity().finish();
    }

    private void goToEditCategory(Long categoryId) {
        startActivity(ManageCategoryActivity.create(context, categoryId));
        getActivity().finish();
    }

}