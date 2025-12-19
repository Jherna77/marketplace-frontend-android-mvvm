package com.jhernandez.frontend.bazaar.domain.model;

import java.util.List;

/*
 * Record representing a Product entity.
 */
public record Product(Long id, Boolean enabled, String name, String description,
                      Double price, Double shipping, List<Category> categories,
                      List<String> imagesUrl, Long shopId, Integer sold, Double rating,
                      Integer ratingCount, Boolean hasDiscount, Double discountPrice,
                      Integer stock) {
}
