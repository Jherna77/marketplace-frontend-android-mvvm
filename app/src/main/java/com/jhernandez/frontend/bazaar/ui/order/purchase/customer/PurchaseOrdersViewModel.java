package com.jhernandez.frontend.bazaar.ui.order.purchase.customer;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ALL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ASCENDING;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for managing purchase orders for a customer.
 * It handles loading, filtering, and navigation events related to purchase orders.
 */
public class PurchaseOrdersViewModel extends BaseViewModel {

    private final OrderRepositoryPort orderRepository;
    private final MutableLiveData<PurchaseOrdersViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> filteredOrders = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToOrderDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private final User user;
    private List<Order> allOrders;
    private String selectedStatus;
    private String selectedOrder;

    public PurchaseOrdersViewModel(OrderRepositoryPort orderRepository, SessionRepositoryPort sessionRepository) {
        this.orderRepository = orderRepository;
        this.user = sessionRepository.getSessionUser();
    }

    public LiveData<PurchaseOrdersViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<Order>> getFilteredOrders() {
        return filteredOrders;
    }
    public LiveData<Long> goToOrderDetailEvent() {
        return _goToOrderDetail;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void initViewState() {
        viewState.setValue(new PurchaseOrdersViewState(false, false));
        selectedStatus = TAG_ALL;
        selectedOrder = TAG_ASCENDING;
        findPurchaseOrdersByUserId(user.id());
    }

    private void updateFilter(Boolean filter) {
        viewState.setValue(viewState.getValue().withFilter(filter));
    }

    private void updateResults(Boolean results) {
        viewState.setValue(viewState.getValue().withResults(results));
    }

    public void findPurchaseOrdersByUserId(Long userId) {
        Log.d("CustomerOrdersViewModel", "Loading orders for user with id " + userId);
        orderRepository.findPurchaseOrdersByUserId(userId, typeCallback(
                filteredOrders,
                ordersResult -> {
                    allOrders = ordersResult;
                    updateResults(ordersResult != null && !ordersResult.isEmpty());
                },
                error -> allOrders = new ArrayList<>()));
    }

    public void onCheckOrderSelected(Long orderId) {
        _goToOrderDetail.setValue(orderId);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

    public void onFilterResultsSelected() {
        updateFilter(!viewState.getValue().filterEnabled());
    }

    public void onStatusFilterSelected(String status) {
        selectedStatus = status;
        applyFilters();
    }

    public void onOrderSelected(String order) {
        selectedOrder = order;
        applyFilters();
    }

    private void applyFilters() {
        if (allOrders == null) { return; }
        List<Order> filtered = new ArrayList<>();

        for (Order order : allOrders) {
            Boolean matchesStatus = selectedStatus.equals(TAG_ALL) ||
                    (selectedStatus.equals(order.status()));

            if (matchesStatus) { filtered.add(order); }
        }

        if (selectedOrder.equals(TAG_ASCENDING)) {
            filtered.sort(Comparator.comparing(Order::id));
        } else {
            filtered.sort(Comparator.comparing(Order::id).reversed());
        }

        updateResults(filtered != null && !filtered.isEmpty());
        filteredOrders.setValue(filtered);
    }

}
