package com.jhernandez.frontend.bazaar.ui.product.shop;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for managing the state and data of the ShopProductsFragment.
 * It handles loading products for a specific shop, managing view states, and navigation events.
 */
public class ShopProductsViewModel extends BaseViewModel {

    private final ProductRepositoryPort productRepository;
    private final MutableLiveData<ShopProductsViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<User> shop = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToEditProduct = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAddProduct = new MutableLiveData<>();

    public ShopProductsViewModel(ProductRepositoryPort productRepository, SessionRepositoryPort sessionRepository) {
        this.productRepository = productRepository;
        this.shop.setValue(sessionRepository.getSessionUser());
    }

    public LiveData<ShopProductsViewState> getViewState() {
        return viewState;
    }

    public LiveData<User> getShop() {
        return shop;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Long> getGoToEditProductEvent() {
        return _goToEditProduct;
    }

    public LiveData<Boolean> getGoToAddProductEvent() {
        return _goToAddProduct;
    }

    public void setViewState() {
        viewState.setValue(new ShopProductsViewState(
                false,
                true
        ));
        findShopProducts(shop.getValue().id());
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateProducts(Boolean hasProducts) {
        viewState.setValue(viewState.getValue().withProducts(hasProducts));
    }

    // Load user products from the server
    public void findShopProducts(Long shopId) {
        showLoading(true);
        Log.d("ShopProductsViewModel", "Loading products for user with id " + shopId);
        productRepository.findProductsByUserId(shopId, typeCallback(
                products,
                productsResult -> {
                    handleViewState(productsResult);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));

    }

    private void handleViewState(List<Product> shopProducts) {
        if (shopProducts != null && !shopProducts.isEmpty()) {
            updateProducts(true);
        } else {
            updateProducts(false);
        }
    }

    public void onEditProductSelected(Long productId) {
        _goToEditProduct.setValue(productId);
    }

    public void onAddProductSelected() {
        _goToAddProduct.setValue(true);
    }

}
