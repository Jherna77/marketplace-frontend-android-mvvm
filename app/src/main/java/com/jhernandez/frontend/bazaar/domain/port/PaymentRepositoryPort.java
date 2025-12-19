package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Payment;

/*
 * Interface representing the PaymentRepositoryPort.
 */
public interface PaymentRepositoryPort {

    void createPaymentIntent(Payment payment, TypeCallback<String> callback);

}
