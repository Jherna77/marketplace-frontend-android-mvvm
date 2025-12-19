package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;

import java.util.List;

/*
 * Interface representing the PreferencesRepositoryPort.
 */
public interface PreferencesRepositoryPort {

    void findUserFavouriteProducts(Long userId, TypeCallback<List<Product>> callback);
    void addProductToFavourites(Long userId, Long productId, SuccessCallback callback);
    void removeProductFromFavourites(Long userId, Long productId, SuccessCallback callback);
    void isFavouriteProduct(Long userId, Long productId, TypeCallback<Boolean> callback);
    void findUserFavouriteCategories(Long userId, TypeCallback<List<Category>> callback);

}
