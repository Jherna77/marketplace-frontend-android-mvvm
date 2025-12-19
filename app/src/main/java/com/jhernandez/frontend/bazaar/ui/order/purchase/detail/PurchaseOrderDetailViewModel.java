package com.jhernandez.frontend.bazaar.ui.order.purchase.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.CANCELLED;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for managing the details of a specific purchase order.
 * It handles loading order information, cancelling the order, and navigation events.
 */
public class PurchaseOrderDetailViewModel extends BaseViewModel {

    private final OrderRepositoryPort orderRepository;
    private final UserRepositoryPort userRepository;
    private final MutableLiveData<PurchaseOrderDetailViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Order> order = new MutableLiveData<>();
    private final MutableLiveData<User> shop = new MutableLiveData<>();
    private final MutableLiveData<Item> item = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToLeaveReview = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public PurchaseOrderDetailViewModel(OrderRepositoryPort orderRepository, UserRepositoryPort userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public LiveData<PurchaseOrderDetailViewState> getViewState() {
        return viewState;
    }
    public LiveData<Order> getOrder() {
        return order;
    }
    public LiveData<User> getShop() {
        return shop;
    }
    public LiveData<Item> getItem() {
        return item;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<Boolean> goToLeaveReviewEvent() {
        return _goToLeaveReview;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void setViewState(Long orderId) {
        viewState.setValue(new PurchaseOrderDetailViewState(false));
        findOrderById(orderId);
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    public void findOrderById(Long id) {
        showLoading(true);
        Log.d("PurchaseOrderDetailViewModel", "Loading order with id " + id);
        orderRepository.findOrderById(id, typeCallback(
                order,
                orderResult -> {
                    item.postValue(orderResult.item());
                    findUserById(orderResult.item().getProduct().shopId());
                },
                error -> showLoading(false)
        ));
    }

    public void findUserById(Long id) {
        Log.d("PurchaseOrderDetailViewModel", "Loading user with id " + id);
        userRepository.findUserById(id, typeCallback(
                shop,
                shopResult -> showLoading(false),
                error -> showLoading(false)
        ));
    }

    private void cancelOrder() {
        showLoading(true);
        Log.d("SaleOrderDetailViewModel", "Cancelling order...");
        orderRepository.updateOrderStatus(order.getValue().id(), CANCELLED, new TypeCallback<>() {
            @Override
            public void onSuccess(Order orderResult) {
                order.postValue(orderResult);
                apiError.postValue(null);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showLoading(false);
            }
        });
    }

    public void onCancelOrderConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { cancelOrder(); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onLeaveReviewSelected () {
        _goToLeaveReview.setValue(true);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
