package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.OrderMapper;
import com.jhernandez.frontend.bazaar.data.model.OrderStatusDto;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing order-related operations.
 */
@RequiredArgsConstructor
public class OrderRepository implements OrderRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createOrderFromCart(Long userId, SuccessCallback callback) {
        apiService.createOrderFromCart(userId)
                .enqueue(CallbackDelegator.delegate("createOrderFromCart", callback));
    }

    @Override
    public void findPurchaseOrdersByUserId(Long userId, TypeCallback<List<Order>> callback) {
        apiService.findPurchaseOrdersByUserId(userId)
                .enqueue(CallbackDelegator.delegate(
                        "getPurchaseOrdersByUserId",
                        response ->
                                callback.onSuccess(OrderMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findSaleOrdersByUserId(Long userId, TypeCallback<List<Order>> callback) {
        apiService.findSaleOrdersByUserId(userId)
                .enqueue(CallbackDelegator.delegate(
                        "getSaleOrdersByUserId",
                        response ->
                                callback.onSuccess(OrderMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findOrderById(Long id, TypeCallback<Order> callback) {
        apiService.findOrderById(id)
                .enqueue(CallbackDelegator.delegate(
                        "getOrderById",response ->
                                callback.onSuccess(OrderMapper.toDomain(response)),
                                callback::onError));
    }

    @Override
    public void updateOrderStatus(Long id, String status, TypeCallback<Order> callback) {
        apiService.updateOrderStatus(id, new OrderStatusDto(status))
                .enqueue(CallbackDelegator.delegate(
                        "updateOrderStatus", response ->
                                callback.onSuccess(OrderMapper.toDomain(response)),
                                callback::onError));
    }

}
