package com.jhernandez.frontend.bazaar.data.model;

/*
 * Data transfer object for item information.
 */
public record ItemDto(Long id, ProductDto product, Double salePrice, Double saleShipping, Integer quantity, Double totalPrice) {

}
