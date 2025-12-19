package com.jhernandez.frontend.bazaar.data.model;

/*
 * Data transfer object for review information.
 */
public record ReviewDto(Long id, String author, Long orderId, String comment, Integer rating, String reviewDate) {
}
