package com.jhernandez.frontend.bazaar.data.model;

/*
 * Data transfer object for category information.
 */
public record CategoryDto(Long id, Boolean enabled, String name, String imageUrl) {
}
