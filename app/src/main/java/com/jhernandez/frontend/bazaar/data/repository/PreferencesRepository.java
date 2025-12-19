package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.CategoryMapper;
import com.jhernandez.frontend.bazaar.data.mapper.ProductMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.port.PreferencesRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing preferences-related operations.
 */
@RequiredArgsConstructor
public class PreferencesRepository implements PreferencesRepositoryPort {

    private final ApiService apiService;

    @Override
    public void findUserFavouriteProducts(Long userId, TypeCallback<List<Product>> callback) {
        apiService.findUserFavouriteProducts(userId)
                .enqueue(CallbackDelegator.delegate(
                        "getUserFavouriteProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError
                ));
    }

    @Override
    public void addProductToFavourites(Long userId, Long productId, SuccessCallback callback) {
        apiService.addProductToFavourites(userId, productId)
                .enqueue(CallbackDelegator.delegate("addProductToFavourites", callback));
    }

    @Override
    public void removeProductFromFavourites(Long userId, Long productId, SuccessCallback callback) {
        apiService.removeProductFromFavourites(userId, productId)
                .enqueue(CallbackDelegator.delegate("removeProductFromFavourites", callback));
    }

    @Override
    public void isFavouriteProduct(Long userId, Long productId, TypeCallback<Boolean> callback) {
        apiService.isFavouriteProduct(userId, productId)
                .enqueue(CallbackDelegator.delegate(
                        "isFavouriteProduct",
                                callback::onSuccess,
                                callback::onError
                ));
    }

    @Override
    public void findUserFavouriteCategories(Long userId, TypeCallback<List<Category>> callback) {
        apiService.findUserFavouriteCategories(userId)
                .enqueue(CallbackDelegator.delegate(
                        "getUserFavouriteCategories",
                        response ->
                                callback.onSuccess(CategoryMapper.toDomainList(response)),
                                callback::onError
                ));
    }
}
