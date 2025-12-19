package com.jhernandez.frontend.bazaar.ui.cart;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.port.CartRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for CartFragment.
 * Manages the data and business logic related to the shopping cart.
 * Interacts with the CartRepositoryPort to perform operations.
 */
public class CartViewModel extends BaseViewModel {

    public static final Integer MIN_QUANTITY = 1;
    public static final Integer MAX_QUANTITY = 5;

    private final CartRepositoryPort cartRepository;
    private final MutableLiveData<CartViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Item>> items = new MutableLiveData<>();
    private final MutableLiveData<Double> priceTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> shippingTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> cartTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Long> _showItemQuantityDialog = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _clearCart = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToHome = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToProductDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToPayment = new MutableLiveData<>();
    private final Long userId;

    public CartViewModel (CartRepositoryPort cartRepository, SessionRepositoryPort sessionRepository) {
        this.cartRepository = cartRepository;
        this.userId = sessionRepository.getSessionUser().id();
    }

    public LiveData<CartViewState> getViewState() {
        return viewState;
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<Double> getPriceTotal() {
        return priceTotal;
    }

    public LiveData<Double> getShippingTotal() {
        return shippingTotal;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public LiveData<Long> showItemQuantityDialogEvent() {
        return _showItemQuantityDialog;
    }

    public LiveData<Boolean> clearCartEvent() {
        return _clearCart;
    }

    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }

    public LiveData<Boolean> goToHomeEvent() {
        return _goToHome;
    }

    public LiveData<Long> goToProductDetailEvent() {
        return _goToProductDetail;
    }

    public LiveData<Boolean> goToPaymentEvent() {
        return _goToPayment;
    }

    public void setViewState() {
        viewState.setValue(new CartViewState(
                false,
                true
        ));

        loadUserCart(userId);
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateEmpty(boolean isEmpty) {
        viewState.setValue(viewState.getValue().withEmpty(isEmpty));
    }

    public void loadUserCart(Long userId) {
        showLoading(true);
        Log.d("CartViewModel", "Loading cart for user with id " + userId);
        cartRepository.loadUserCart(userId, typeCallback(
                items,
                itemsResult -> {
                    updateCart(itemsResult);
                    showLoading(false);
                },
                error -> showLoading(false)));
    }

    public void removeItemFromCart(Long itemId) {
        showLoading(true);
        Log.d("CartViewModel", "Removing item from cart for user with id " + userId);
        cartRepository.removeItemFromUserCart(userId, itemId, typeCallback(
                items,
                itemsResult -> {
                    updateCart(itemsResult);
                    showLoading(false);
                },
                error -> showLoading(false)));
    }

    public void updateItemQuantity(Long itemId, Integer quantity) {
        showLoading(true);
        Log.d("CartViewModel", "Updating item quantity for user with id " + userId);
        cartRepository.updateItemQuantity(userId, itemId, quantity, typeCallback(
                items,
                itemsResult -> {
                    updateCart(itemsResult);
                    showLoading(false);
                },
                error -> showLoading(false)));
    }

    public void clearUserCart() {
        showLoading(true);
        Log.d("CartViewModel", "Clearing cart for user with id " + userId);
        cartRepository.clearUserCart(userId, typeCallback(
                items,
                itemsResult -> {
                    updateCart(itemsResult);
                    showLoading(false);
                },
                error -> showLoading(false)));
    }

    private void updateCart(List<Item> items) {
        updateEmpty(items == null || items.isEmpty());
        if (items != null && !items.isEmpty()) { calculate(items); }
    }

    public void onClearCartSelected() {
        _clearCart.setValue(true);
    }

    public void onClearCartConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { clearUserCart(); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    private void calculate(List<Item> items) {
        Log.d("CartViewModel", "Calculating cart");
        priceTotal.setValue(getPriceAmount(items));
        shippingTotal.setValue(getShippingAmount(items));
        cartTotal.postValue(priceTotal.getValue() + shippingTotal.getValue());
    }

    private Double getPriceAmount(List<Item> items) {
        return items.stream()
                .mapToDouble(item ->
                        item.getQuantity() * (item.getProduct().hasDiscount()
                                ? item.getProduct().discountPrice()
                                : item.getProduct().price()))
                .sum();
    }

    private Double getShippingAmount(List<Item> items) {
        return items.stream()
                .mapToDouble(item ->
                        item.getProduct().shipping())
                .sum();
    }

    public void onKeepBuyingSelected() {
        _goToHome.setValue(true);
    }

    public void onItemSelected(Long productId) {
        _goToProductDetail.setValue(productId);
    }

    public void onRemoveItemSelected(Long itemId) {
        removeItemFromCart(itemId);
    }

    public void onItemQuantitySelected(Long itemId) {
        _showItemQuantityDialog.setValue(itemId);
    }

    public void onItemQuantityChanged(Long itemId, Integer quantity) {
        updateItemQuantity(itemId, quantity);
    }

    public void onProcessOrderSelected() {
        _goToPayment.setValue(true);
    }

}
