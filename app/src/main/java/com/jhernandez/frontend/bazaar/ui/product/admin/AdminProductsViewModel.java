package com.jhernandez.frontend.bazaar.ui.product.admin;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ALL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ASCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_DISABLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ENABLED;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for managing products in the admin dashboard.
 * It handles loading, filtering, ordering, and editing of products.
 */
public class AdminProductsViewModel extends BaseViewModel {

    private final ProductRepositoryPort productRepository;
    private final MutableLiveData<AdminProductsViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToEditProduct = new MutableLiveData<>();
    private List<Product> allProducts;
    private String selectedStatus;
    private String selectedOrder;

    public AdminProductsViewModel(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public LiveData<AdminProductsViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }
    public LiveData<Long> goToEditProductEvent() {
        return _goToEditProduct;
    }

            public void initViewState() {
                viewState.setValue(new AdminProductsViewState(false));
                selectedStatus = TAG_ALL;
                selectedOrder = TAG_ASCENDING;
                findAllProducts();
            }

            private void updateFilter(Boolean filter) {
                viewState.setValue(viewState.getValue().withFilter(filter));
            }

    // Load products from the server
    public void findAllProducts() {
        Log.d("AdminProductViewModel", "Loading all products...");
        productRepository.findAllProducts(new TypeCallback<>() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts = products;
                filteredProducts.postValue(products);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                allProducts = new ArrayList<>();
                filteredProducts.postValue(allProducts);
            }
        });
    }

    public void onEditProductSelected(Long productId) {
        _goToEditProduct.setValue(productId);
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
        List<Product> filtered = new ArrayList<>();

        for (Product product : allProducts) {
            Boolean matchesStatus = selectedStatus.equals(TAG_ALL) ||
                    (selectedStatus.equals(TAG_ENABLED) && product.enabled()) ||
                    (selectedStatus.equals(TAG_DISABLED) && !product.enabled());

            if (matchesStatus) { filtered.add(product); }
        }

        if (selectedOrder.equals(TAG_ASCENDING)) {
            filtered.sort(Comparator.comparing(Product::name));
        } else {
            filtered.sort(Comparator.comparing(Product::name).reversed());
        }

        filteredProducts.setValue(filtered);
    }

}
