package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.PaymentRequestDto;
import com.jhernandez.frontend.bazaar.domain.model.Payment;

/**
 * Mapper class for converting between Payment and PaymentRequestDto.
 */
public class PaymentMapper {
    public static PaymentRequestDto toDto(Payment payment) {
        return new PaymentRequestDto(payment.amount(), payment.currency());
    }

}
