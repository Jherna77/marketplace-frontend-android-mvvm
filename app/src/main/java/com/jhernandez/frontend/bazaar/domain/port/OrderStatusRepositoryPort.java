package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;

/*
 * Interface representing the OrderStatusRepositoryPort.
 */
public interface OrderStatusRepositoryPort {

    void findAllOrderStatuses(TypeCallback<String[]> callback);

}
