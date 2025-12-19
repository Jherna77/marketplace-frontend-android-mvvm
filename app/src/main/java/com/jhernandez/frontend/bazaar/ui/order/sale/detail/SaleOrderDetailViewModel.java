package com.jhernandez.frontend.bazaar.ui.order.sale.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.OrderStatusRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for managing the details of a specific sale order.
 * It handles loading order information, updating the order status, and navigation events.
 */
public class SaleOrderDetailViewModel extends BaseViewModel {

    private final OrderRepositoryPort orderRepository;
    private final UserRepositoryPort userRepository;
    private final OrderStatusRepositoryPort orderStatusRepository;
    private final MutableLiveData<SaleOrderDetailViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Order> order = new MutableLiveData<>();
    private final MutableLiveData<User> customer = new MutableLiveData<>();
    private final MutableLiveData<Item> item = new MutableLiveData<>();
    private final MutableLiveData<String> orderStatus = new MutableLiveData<>();
    private final MutableLiveData<String[]> _showOrderStatusDialog = new MutableLiveData<>();
    private final MutableLiveData<String> _confirmStatusUpdate = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private String[] allOrderStatuses;

    public SaleOrderDetailViewModel(OrderRepositoryPort orderRepository, UserRepositoryPort userRepository,
                                    OrderStatusRepositoryPort orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public LiveData<SaleOrderDetailViewState> getViewState() {
        return viewState;
    }
    public LiveData<Order> getOrder() {
        return order;
    }
    public LiveData<User> getCustomer() {
        return customer;
    }
    public LiveData<Item> getItem() {
        return item;
    }
    public LiveData<String> getOrderStatus() {
        return orderStatus;
    }
    public LiveData<String[]> showOrderStatusDialogEvent() {
        return _showOrderStatusDialog;
    }
    public LiveData<String> confirmStatusUpdateEvent() {
        return _confirmStatusUpdate;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void setViewState(Long orderId) {
        viewState.setValue(new SaleOrderDetailViewState(false, false));
        findOrderById(orderId);
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void showErrors(Boolean hasErrors) {
        viewState.setValue(viewState.getValue().withError(hasErrors));
    }

    private void findOrderById(Long id) {
        showLoading(true);
        Log.d("SaleOrderDetailViewModel", "Loading order with id " + id);
        orderRepository.findOrderById(id, typeCallback(
                order,
                orderResult -> {
                    item.postValue(orderResult.item());
                    orderStatus.postValue(orderResult.status());
                    findUserById(orderResult.customerId());
                },
                error -> {
                    showErrors(true);
                    showLoading(false);
                }
        ));
    }

    private void findUserById(Long id) {
        Log.d("SaleOrderDetailViewModel", "Loading user with id " + id);
        userRepository.findUserById(id, typeCallback(
                customer,
                customerResult -> {
                    showErrors(false);
                    showLoading(false);
                },
                error -> {
                    showErrors(true);
                    showLoading(false);
                }
        ));
    }

    private void findAllOrderStatuses() {
        showLoading(true);
        Log.d("SaleOrderDetailViewModel", "Loading all order statuses...");
        orderStatusRepository.findAllOrderStatuses(new TypeCallback<>() {
            @Override
            public void onSuccess(String[] statusResult) {
                allOrderStatuses = statusResult;
                _showOrderStatusDialog.postValue(statusResult);
                showErrors(false);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showErrors(true);
                showLoading(false);
            }
        });
    }

    private void updateOrderStatus() {
        showLoading(true);
        Log.d("SaleOrderDetailViewModel", "Updating order status to " + orderStatus.getValue());
        orderRepository.updateOrderStatus(order.getValue().id(), orderStatus.getValue(), new TypeCallback<>() {
            @Override
            public void onSuccess(Order orderResult) {
                order.postValue(orderResult);
                orderStatus.postValue(orderResult.status());
                apiError.postValue(null);
                showErrors(false);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showErrors(true);
                showLoading(false);
            }
        });
    }

    public void onSelectStatusClicked() {
        if (allOrderStatuses == null) { findAllOrderStatuses(); }
        else { _showOrderStatusDialog.postValue(allOrderStatuses); }
    }

    public void onOrderStatusSelected(String status) {
        orderStatus.setValue(status);
    }

    public void onUpdateStatusSelected() {
        _confirmStatusUpdate.setValue(orderStatus.getValue());
    }

    public void onChangeStatusConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { updateOrderStatus(); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
