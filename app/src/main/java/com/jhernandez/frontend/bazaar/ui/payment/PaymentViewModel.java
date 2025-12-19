package com.jhernandez.frontend.bazaar.ui.payment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Payment;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.CartRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.PaymentRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;

/*
 * ViewModel for managing the payment process.
 * It handles loading user cart, creating orders, and processing payments.
 */
public class PaymentViewModel extends BaseViewModel {

    public static final String PUBLIC_KEY = "pk_test_51RSwkqQQCVuiX04gAt0xqmHbDp4AKqKKRGaClxHkv9fhzNFDEUgXH5IvaqgyejIBRZ84muHMBahPzdNk8bWhKWs800DcGeLbQc";

    private final CartRepositoryPort cartRepository;
    private final OrderRepositoryPort orderRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final List<Item> items = new ArrayList<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Double> productsTotal = new MutableLiveData<>();
    private final MutableLiveData<Double> shippingTotal = new MutableLiveData<>();
    private final MutableLiveData<Double> orderTotal = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _showPaymentError = new MutableLiveData<>();

    public PaymentViewModel (CartRepositoryPort cartRepository, OrderRepositoryPort orderRepository,
                             PaymentRepositoryPort paymentRepository, SessionRepositoryPort sessionRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.user.setValue(sessionRepository.getSessionUser());
        loadUserCart();
    }

    public LiveData<User> getUser() {
        return user;
    }
    public LiveData<Double> getProductsTotal() {
        return productsTotal;
    }
    public LiveData<Double> getShippingTotal() {
        return shippingTotal;
    }
    public LiveData<Double> getOrderTotal() {
        return orderTotal;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<Boolean> showPaymentErrorEvent() {
        return _showPaymentError;
    }

    // Load session user cart items from server
    public void loadUserCart() {
        Log.d("PaymentViewModel", "Loading cart for user with id " + user.getValue().id());
        cartRepository.loadUserCart(user.getValue().id(), new TypeCallback<>() {
            @Override
            public void onSuccess(List<Item> result) {
                items.addAll(result);
                apiError.postValue(null);
                calculate();
            }

            @Override
            public void onError(ApiErrorResponse error) {
                PaymentViewModel.this.apiError.postValue(error);
            }
        });
    }

    // Create order from session user cart
    public void createOrderFromCart() {
        Log.d("PaymentViewModel", "Creating order from cart for user with id " + user.getValue().id());
        orderRepository.createOrderFromCart(user.getValue().id(), successCallback());
    }

    private void createPaymentIntent(PaymentSheet paymentSheet) {
        Log.d("PaymentViewModel", "Creating payment intent");
        paymentRepository.createPaymentIntent(new Payment(
                orderTotal.getValue(), "eur"), new TypeCallback<>() {
            @Override
            public void onSuccess(String paymentIntentClientSecret) {
                PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("BaZaaR");
                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
            }
        });
    }

    public void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            createOrderFromCart();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            cancelledAction.setValue(true);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            _showPaymentError.setValue(true);
        }
    }

    public void onMakePaymentSelected(PaymentSheet paymentSheet) {
        createPaymentIntent(paymentSheet);
    }

    public void calculate() {
        Log.d("PaymentViewModel", "Calculating cart");
        productsTotal.setValue(getProductsAmount());
        shippingTotal.setValue(getShippingAmount());
        orderTotal.setValue(productsTotal.getValue() + shippingTotal.getValue());
    }

    private Double getProductsAmount() {
        return items.stream()
                    .mapToDouble(item ->
                        item.getQuantity() * (item.getProduct().hasDiscount()
                                ? item.getProduct().discountPrice()
                                : item.getProduct().price()))
                    .sum();
    }

    private Double getShippingAmount() {
        return items.stream()
                    .mapToDouble(item ->
                        item.getProduct().shipping())
                    .sum();
    }

}
