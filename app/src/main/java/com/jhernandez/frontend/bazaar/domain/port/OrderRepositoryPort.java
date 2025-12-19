package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Order;

import java.util.List;

/*
 * Interface representing the OrderRepositoryPort.
 */
public interface OrderRepositoryPort {

    void createOrderFromCart(Long userId, SuccessCallback callback);
    void findPurchaseOrdersByUserId(Long userId, TypeCallback<List<Order>> callback);
    void findSaleOrdersByUserId(Long userId, TypeCallback<List<Order>> callback);
    void findOrderById(Long id, TypeCallback<Order> callback);
    void updateOrderStatus(Long id, String status, TypeCallback<Order> callback);

}
