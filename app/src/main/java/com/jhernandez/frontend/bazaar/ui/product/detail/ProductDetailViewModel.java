package com.jhernandez.frontend.bazaar.ui.product.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.CartRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.PreferencesRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.Objects;

/*
 * ViewModel for managing the state and operations related to product details.
 * It handles loading product information, managing favorites, adding to cart, and navigation events.
 */
public class ProductDetailViewModel extends BaseViewModel {

    private static final Double FREE_SHIPPING = 0.0;

    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;
    private final CartRepositoryPort cartRepository;
    private final PreferencesRepositoryPort preferencesRepository;
    private final User user;
    private final MutableLiveData<ProductDetailViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<User> shop = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToLogin = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToProductReviews = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public ProductDetailViewModel(ProductRepositoryPort productRepository, UserRepositoryPort userRepository, CartRepositoryPort cartRepository,
                                  PreferencesRepositoryPort preferencesRepository, SessionRepositoryPort sessionRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.preferencesRepository = preferencesRepository;
        this.user = sessionRepository.getSessionUser();
    }

    public LiveData<ProductDetailViewState> getViewState() {
        return viewState;
    }
    public LiveData<Product> getProduct() {
        return product;
    }
    public LiveData<User> getShop() {
        return shop;
    }
    public LiveData<Boolean> goToLoginEvent() {
        return _goToLogin;
    }
    public LiveData<Long> goToProductReviewsEvent() {
        return _goToProductReviews;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void setViewState(Long productId) {
        viewState.setValue(new ProductDetailViewState(
                false,
                false,
                false
        ));
        findProductById(productId);
    }

    private void updateFavourite(Boolean favourite) {
        viewState.setValue(viewState.getValue().withFavourite(favourite));
    }
    private void updateFreeShipping(Boolean freeShipping) {
        viewState.setValue(viewState.getValue().withFreeShipping(freeShipping));
    }
    private void updateDiscount(Boolean discount) {
        viewState.setValue(viewState.getValue().withDiscount(discount));
    }

    // Load product from the server
    public void findProductById(Long id) {
        Log.d("ProductDetailViewModel", "Loading product with id " + id);
        productRepository.findProductById(id, new TypeCallback<>() {
            @Override
            public void onSuccess(Product productResult) {
                product.postValue(productResult);
                apiError.postValue(null);
                updateDiscount(productResult.hasDiscount());
                updateFreeShipping(Objects.equals(productResult.shipping(), FREE_SHIPPING));
                if (user != null) { isFavouriteProduct(productResult.id()); }
                findShopById(productResult.shopId());
            }
            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                product.postValue(null);
            }
        });
    }

    // Load shop from the server
    private void findShopById(Long shopId) {
        Log.d("ProductDetailViewModel", "Loading shop with id " + shopId);
        userRepository.findUserById(shopId, typeCallback(shop));
    }

    // Add item to user cart
    private void addItemToUserCart(Item item) {
        Log.d("ProductDetailViewModel", "Adding item to cart for customer with id " + user.id());
        cartRepository.addItemToUserCart(user.id(), item, successCallback());
    }

    private void isFavouriteProduct(Long productId) {
        Log.d("ProductDetailViewModel", "Checking if product is favourite");
        preferencesRepository.isFavouriteProduct(
                user.id(),
                productId,
                new TypeCallback<>() {
                    @Override
                    public void onSuccess(Boolean isFavourite) {
                        updateFavourite(isFavourite);
                        apiError.postValue(null);
                    }

                    @Override
                    public void onError(ApiErrorResponse error) {
                        apiError.postValue(error);
                        updateFavourite(false);
                    }
                });
    }

    private void addProductToFavourites() {
        Log.d("ProductDetailViewModel", "Adding product to favorites for customer with id " + user.id());
        preferencesRepository.addProductToFavourites(
                user.id(),
                product.getValue().id(),
                new SuccessCallback(){
                    @Override
                    public void onSuccess() {
                        updateFavourite(true);
                        apiError.postValue(null);
                    }

                    @Override
                    public void onError(ApiErrorResponse error) {
                        apiError.postValue(error);
                    }
                });
    }

    private void removeProductFromFavourites() {
        Log.d("ProductDetailViewModel", "Removing product from favorites for customer with id " + user.id());
        preferencesRepository.removeProductFromFavourites(
                user.id(),
                product.getValue().id(),
                new SuccessCallback(){
                    @Override
                    public void onSuccess() {
                        updateFavourite(false);
                        apiError.postValue(null);
                    }

                    @Override
                    public void onError(ApiErrorResponse error) {
                        apiError.postValue(error);
                    }
                });
    }

    public void onAddToCartSelected() {
        if (user == null) { _goToLogin.setValue(true); }
        else {
            Double salePrice = product.getValue().hasDiscount()
                    ? product.getValue().discountPrice()
                    :  product.getValue().price();
            addItemToUserCart(new Item(
                    null,
                    product.getValue(),
                    salePrice,
                    product.getValue().shipping(),
                    1,
                    salePrice + product.getValue().shipping()
            ));
        }
    }

    public void onFavSelected() {
        if (user == null) { _goToLogin.setValue(true); }
        else if (viewState.getValue().isFavourite()) { removeProductFromFavourites(); }
        else { addProductToFavourites(); }
    }

    public void onRatingSelected() {
        _goToProductReviews.setValue(product.getValue().id());
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }
}
