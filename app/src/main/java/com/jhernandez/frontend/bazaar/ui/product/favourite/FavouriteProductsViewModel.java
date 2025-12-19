package com.jhernandez.frontend.bazaar.ui.product.favourite;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.port.PreferencesRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for managing the user's favorite products.
 * It handles loading the favorite products, navigation to product details, and UI state updates.
 */
public class FavouriteProductsViewModel extends BaseViewModel {

    private final PreferencesRepositoryPort preferencesRepository;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToProductDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _showInfoTV = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public FavouriteProductsViewModel(PreferencesRepositoryPort preferencesRepository, SessionRepositoryPort sessionRepository) {
        this.preferencesRepository = preferencesRepository;
        findUserFavouriteProducts(sessionRepository.getSessionUser().id());
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }
    public LiveData<Long> goToProductDetailEvent() {
        return _goToProductDetail;
    }
    public LiveData<Boolean> showInfoTVEvent() {
        return _showInfoTV;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    private void findUserFavouriteProducts(Long userId) {
        Log.d("FavouriteProductsViewModel", "Getting favourite products for user with ID " + userId);
        preferencesRepository.findUserFavouriteProducts(userId, typeCallback(
                products,
                productsResult ->
                        _showInfoTV.setValue(productsResult == null || productsResult.isEmpty()),
                error -> {}
                ));
    }

    public void onProductSelected(Long productId) {
        _goToProductDetail.setValue(productId);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}