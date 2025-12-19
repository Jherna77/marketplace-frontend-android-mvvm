package com.jhernandez.frontend.bazaar.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.PreferencesRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * ViewModel for the home screen of the Bazaar app.
 * Manages data related to products, categories, and user preferences.
 */
public class HomeViewModel extends BaseViewModel {

    private static final int RANDOM_CATEGORIES = 3;

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final SessionRepositoryPort sessionRepository;
    private final PreferencesRepositoryPort preferencesRepository;
    private final MutableLiveData<HomeViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> favouriteProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> discountedProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> recentProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> topSellingProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> topRatedProducts = new MutableLiveData<>();
    private final MutableLiveData<List<HomeCategory>> homeCategories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAccount = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToProductDetail = new MutableLiveData<>();

    public HomeViewModel(ProductRepositoryPort productRepository, CategoryRepositoryPort categoryRepository,
                         SessionRepositoryPort sessionRepository, PreferencesRepositoryPort preferencesRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sessionRepository = sessionRepository;
        this.preferencesRepository = preferencesRepository;
    }

    public LiveData<HomeViewState> getViewState() {
        return viewState;
    }
    public LiveData<User> getUser() {
        return user;
    }
    public LiveData<List<Product>> getFavouriteProducts() {
        return favouriteProducts;
    }
    public LiveData<List<Product>> getDiscountedProducts() {
        return discountedProducts;
    }
    public LiveData<List<Product>> getRecentProducts() {
        return recentProducts;
    }
    public LiveData<List<Product>> getTopSellingProducts() {
        return topSellingProducts;
    }
    public LiveData<List<Product>> getTopRatedProducts() {
        return topRatedProducts;
    }
    public LiveData<List<HomeCategory>> getHomeCategories() {
        return homeCategories;
    }
    public LiveData<Boolean> goToAccountEvent() {
        return _goToAccount;
    }
    public LiveData<Long> goToProductDetailEvent() {
        return _goToProductDetail;
    }

    public void setViewState() {
        Boolean isAuthenticated = sessionRepository.getSessionUser() != null;
        viewState.setValue(new HomeViewState(
                false,
                isAuthenticated,
                false
        ));
        showLoading(true);
        findDiscountedProducts();
        findRecentProducts();
        findTopSellingProducts();
        findTopRatedProducts();
        if (isAuthenticated) {
            user.setValue(sessionRepository.getSessionUser());
            findUserFavouriteProducts();
            findFavouriteCategories();
        } else {
            findRandomCategories();
        }
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void hasFavourites(Boolean hasFavourites) {
        viewState.setValue(viewState.getValue().withFavourites(hasFavourites));
    }

    private void findUserFavouriteProducts() {
        Log.d("HomeViewModel", "Finding favourite products for user with ID : " + user.getValue().id());
        preferencesRepository.findUserFavouriteProducts(user.getValue().id(), typeCallback(
                favouriteProducts,
                result ->
                        hasFavourites(result != null && !result.isEmpty()),
                error -> {}
        ));
    }

    public void findDiscountedProducts() {
        Log.d("HomeViewModel", "Finding discounted products");
        productRepository.findDiscountedProducts(typeCallback(discountedProducts));
    }

    public void findRecentProducts() {
        Log.d("HomeViewModel", "Finding recently added products");
        productRepository.findRecentProducts(typeCallback(recentProducts));
    }

    public void findTopSellingProducts() {
        Log.d("HomeViewModel", "Finding top selling products");
        productRepository.findTopSellingProducts(typeCallback(topSellingProducts));
    }

    public void findTopRatedProducts() {
        Log.d("HomeViewModel", "Finding top rated products");
        productRepository.findTopRatedProducts(typeCallback(topRatedProducts));
    }

    private void findFavouriteCategories() {
        Log.d("HomeViewModel", "Finding favourite categories for user with ID : " + user.getValue().id());
        preferencesRepository.findUserFavouriteCategories(user.getValue().id(), new TypeCallback<>() {
            @Override
            public void onSuccess(List<Category> categoriesResult) {
                findHomeCategoryProducts(categoriesResult);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showLoading(false);
            }
        });
    }

    private void findRandomCategories() {
        Log.d("HomeViewModel", "Finding random categories");
        categoryRepository.findRandomCategories(new TypeCallback<>() {
            @Override
            public void onSuccess(List<Category> categoriesResult) {
                findHomeCategoryProducts(categoriesResult);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showLoading(false);
            }
        });
    }

    private void findHomeCategoryProducts(List<Category> categoriesResult) {
        List<HomeCategory> categories = new ArrayList<>();
        final int[] completed = {0};
        for (Category category : categoriesResult) {
            productRepository.findProductsByCategoryId(category.id(), new TypeCallback<>() {
                @Override
                public void onSuccess(List<Product> products) {
                    synchronized (categories) {
                        categories.add(new HomeCategory(category.name(), products));
                        completed[0]++;
                        if (completed[0] == categoriesResult.size()) {
                            homeCategories.postValue(categories);
                            apiError.postValue(null);
                            showLoading(false);
                        }
                    }
                }

                @Override
                public void onError(ApiErrorResponse error) {
                    apiError.postValue(error);
                    homeCategories.postValue(null);
                    showLoading(false);
                }
            });
        }
    }

    public void onAccountSelected() {
        _goToAccount.setValue(true);
    }

    public void onProductSelected(Long productId) {
        _goToProductDetail.setValue(productId);
    }

    @AllArgsConstructor
    @Getter
    public static class HomeCategory {
        private String categoryName;
        private List<Product> categoryProducts;
    }

}
