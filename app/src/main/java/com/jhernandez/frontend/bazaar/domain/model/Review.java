package com.jhernandez.frontend.bazaar.domain.model;

/*
 * Record representing a Review entity.
 */
public record Review(Long id, String author, Long orderId, String comment, Integer rating, String reviewDate) {
}
