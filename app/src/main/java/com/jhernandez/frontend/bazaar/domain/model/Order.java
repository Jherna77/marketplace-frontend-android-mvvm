package com.jhernandez.frontend.bazaar.domain.model;

/*
 * Record representing an Order entity.
 */
public record Order(Long id, String status, Item item, Long customerId, Long shopId, String orderDate) {
}
