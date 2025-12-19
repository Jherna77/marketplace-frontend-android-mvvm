package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.PaymentMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Payment;
import com.jhernandez.frontend.bazaar.domain.port.PaymentRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing payment-related operations.
 */
@RequiredArgsConstructor
public class PaymentRepository implements PaymentRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createPaymentIntent(Payment payment, TypeCallback<String> callback) {
        apiService.createPaymentIntent(PaymentMapper.toDto(payment))
                .enqueue(CallbackDelegator.delegate(
                        "createPaymentIntent",
                        response ->
                                callback.onSuccess(response.clientSecret()),
                                callback::onError));
    }
}
