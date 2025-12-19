package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.port.OrderStatusRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing order status-related operations.
 */
@RequiredArgsConstructor
public class OrderStatusRepository implements OrderStatusRepositoryPort {

    private final ApiService apiService;

    @Override
    public void findAllOrderStatuses(TypeCallback<String[]> callback) {
        apiService.findAllOrderStatuses()
                .enqueue(CallbackDelegator.delegate(
                        "findAllOrderStatuses",
                                callback::onSuccess,
                                callback::onError));
    }
}
