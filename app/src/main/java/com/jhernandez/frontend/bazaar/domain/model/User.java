package com.jhernandez.frontend.bazaar.domain.model;

import java.util.List;

/*
 * Record representing a User entity.
 */
public record User(Long id, Boolean enabled, UserRole role, String email, String password,
                   String name, String surnames, String address, String city, String province,
                   String zipCode, List<Category> favCategories) {
}
