package com.jhernandez.frontend.bazaar.ui.category.admin;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ALL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ASCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_DISABLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ENABLED;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for administering categories.
 * Manages category data, filtering, and navigation events for the admin UI.
 */
public class AdminCategoriesViewModel extends BaseViewModel {

    private final CategoryRepositoryPort categoryRepository;
    private final MutableLiveData<AdminCategoriesViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> filteredCategories = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToEditCategory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAddCategory = new MutableLiveData<>();
    private List<Category> allCategories;
    private String selectedStatus;
    private String selectedOrder;

    public AdminCategoriesViewModel(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public LiveData<AdminCategoriesViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<Category>> getFilteredCategories() {
        return filteredCategories;
    }
    public LiveData<Long> goToEditCategoryEvent() {
        return _goToEditCategory;
    }
    public LiveData<Boolean> goToAddCategoryEvent() {
        return _goToAddCategory;
    }

    public void initViewState() {
        viewState.setValue(new AdminCategoriesViewState(false));
        selectedStatus = TAG_ALL;
        selectedOrder = TAG_ASCENDING;
        findAllCategories();
    }

    private void updateFilter(Boolean filter) {
        viewState.setValue(viewState.getValue().withFilter(filter));
    }

    // Load all categories from the server
    public void findAllCategories() {
        Log.d("AdminCategoriesViewModel", "Loading all categories...");
        categoryRepository.findAllCategories(new TypeCallback<>() {
            @Override
            public void onSuccess(List<Category> categories) {
                allCategories = categories;
                filteredCategories.postValue(categories);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                allCategories = new ArrayList<>();
                filteredCategories.postValue(allCategories);
            }
        });
    }

    public void onEditCategorySelected(Long categoryId) {
        _goToEditCategory.setValue(categoryId);
    }

    public void onAddCategorySelected() {
        _goToAddCategory.setValue(true);
    }

    public void onFilterResultsSelected() {
        updateFilter(!viewState.getValue().filterEnabled());
    }

    public void onStatusFilterSelected(String status) {
        selectedStatus = status;
        applyFilters();
    }

    public void onOrderSelected(String order) {
        selectedOrder = order;
        applyFilters();
    }

    private void applyFilters() {
        List<Category> filtered = new ArrayList<>();

        for (Category category : allCategories) {
            Boolean matchesStatus = selectedStatus.equals(TAG_ALL) ||
                    (selectedStatus.equals(TAG_ENABLED) && category.enabled()) ||
                    (selectedStatus.equals(TAG_DISABLED) && !category.enabled());

            if (matchesStatus) { filtered.add(category); }
        }

        if (selectedOrder.equals(TAG_ASCENDING)) {
            filtered.sort(Comparator.comparing(Category::name));
        } else {
            filtered.sort(Comparator.comparing(Category::name).reversed());
        }

        filteredCategories.setValue(filtered);
    }

}
