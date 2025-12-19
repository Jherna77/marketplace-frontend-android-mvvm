package com.jhernandez.frontend.bazaar.data.model;

/*
 * Data transfer object for order information.
 */
public record OrderDto(Long id, String status, ItemDto item, Long customerId, Long shopId, String orderDate) {
}
