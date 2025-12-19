package com.jhernandez.frontend.bazaar.data.model;

import java.util.List;

/*
 * Data transfer object for product information.
 */
public record ProductDto(Long id, Boolean enabled, String name, String description,
                         Double price, Double shipping, List<CategoryDto> categories,
                         List<String> imagesUrl, Long shopId, Integer sold, Double rating,
                         Integer ratingCount, Boolean hasDiscount, Double discountPrice,
                         Integer stock) {
}
