package com.jhernandez.frontend.bazaar.domain.model;

/*
 * Record representing a Category entity.
 */
public record Category(Long id, Boolean enabled, String name, String imageUrl) {
}