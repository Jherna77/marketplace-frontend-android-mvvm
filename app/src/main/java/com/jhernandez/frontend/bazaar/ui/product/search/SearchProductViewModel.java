package com.jhernandez.frontend.bazaar.ui.product.search;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ALL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_PRICE_ASCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_PRICE_DESCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_PROMO;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_RATING_ASCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_RATING_DESCENDING;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for searching products.
 * It handles loading products, filtering, sorting, and navigation to product details.
 */
public class SearchProductViewModel extends BaseViewModel {

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final MutableLiveData<SearchProductViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> _showResultsInfo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _clearBox = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _resetFiler = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _hideKeyboard = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToProductDetail = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();
    private String query;
    private List<Product> allProducts;
    private String selectedStatus;
    private String selectedOrder;

    public SearchProductViewModel(ProductRepositoryPort productRepository, CategoryRepositoryPort categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public LiveData<SearchProductViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<Product>> getProducts() {
        return filteredProducts;
    }
    public LiveData<List<Category>> getCategories() {
        return categories;
    }
    public LiveData<String> showResultsInfoEvent() {
        return _showResultsInfo;
    }
    public LiveData<Boolean> clearBoxEvent() {
        return _clearBox;
    }
    public LiveData<Boolean> resetFilterEvent() {
        return _resetFiler;
    }
    public LiveData<Boolean> hideKeyboardEvent() {
        return _hideKeyboard;
    }
    public LiveData<Long> goToProductDetailEvent() {
        return _goToProductDetail;
    }
    public LiveData<ValidationError> getValidationError() {
        return validationError;
    }

    public void initViewState() {
        viewState.setValue(new SearchProductViewState(
                false,
                true,
                false
        ));
        findAllEnabledCategories();
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateResults(Boolean hasResults) {
        viewState.setValue(viewState.getValue().withResults(hasResults));
    }

    private void updateFilter(Boolean filter) {
        viewState.setValue(viewState.getValue().withFilter(filter));
    }

    public void findAllEnabledCategories() {
        showLoading(true);
        Log.d("SearchViewModel", "Loading all enabled categories");
        categoryRepository.findAllEnabledCategories(typeCallback(
                categories,
                categoriesResult -> showLoading(false),
                error -> showLoading(false)));
    }

    public void findProductsByCategory(Category category) {
        showLoading(true);
        Log.d("SearchViewModel", "Loading products for category " + category.name());
        productRepository.findProductsByCategoryId(category.id(),typeCallback(
                filteredProducts,
                productsResult -> {
                    query = category.name();
                    handleResults(productsResult);
                    showLoading(false);
                },
                error -> {
                    allProducts = new ArrayList<>();
                    showLoading(false);
                }
                ));
    }

    public void findProductsByQuery(String searchQuery) {
        showLoading(true);
        Log.d("SearchViewModel", "Searching all products that contains " + searchQuery);
        productRepository.findProductsByName(searchQuery, typeCallback(
                filteredProducts,
                productsResult -> {
                    query = searchQuery;
                    handleResults(productsResult);
                    showLoading(false);
                },
                error ->
                    showLoading(false)
                ));
    }

    public void validateQuery (String query) {
        _hideKeyboard.postValue(true);
        if (query.isEmpty()) {
            validationError.postValue(ValidationError.QUERY_EMPTY);
        } else {
            validationError.postValue(null);
            findProductsByQuery(query);
        }
    }

    private void handleResults(List<Product> productsResult) {
        updateResults(productsResult != null && !productsResult.isEmpty());
        allProducts = productsResult;
        _clearBox.postValue(true);
        _showResultsInfo.postValue(query);
        _resetFiler.postValue(true);
        selectedStatus = TAG_ALL;
        selectedOrder = TAG_PRICE_ASCENDING;
        applyFilters();
    }

    public void onCategorySelected(Category category) {
        findProductsByCategory(category);
    }

    public void onProductSelected(Long productId) {
        _goToProductDetail.setValue(productId);
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
        if (allProducts == null) { return; }

        List<Product> filtered = new ArrayList<>();

        for (Product product : allProducts) {
            Boolean matchesStatus = selectedStatus.equals(TAG_ALL) ||
                    (selectedStatus.equals(TAG_PROMO) && product.hasDiscount());

            if (matchesStatus) { filtered.add(product); }
        }

        switch (selectedOrder) {
            case TAG_PRICE_ASCENDING -> filtered.sort(Comparator.comparing(product ->
                    product.hasDiscount() ? product.discountPrice() : product.price()));
            case TAG_PRICE_DESCENDING -> filtered.sort(Comparator.comparing((Product product) ->
                    product.hasDiscount() ? product.discountPrice() : product.price()).reversed());
            case TAG_RATING_ASCENDING -> filtered.sort(Comparator.comparing(Product::rating));
            case TAG_RATING_DESCENDING -> filtered.sort(Comparator.comparing(Product::rating).reversed());
        }
        updateResults(!filtered.isEmpty());
        filteredProducts.postValue(filtered);
    }

}
