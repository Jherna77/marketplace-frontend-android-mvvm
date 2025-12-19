package com.jhernandez.frontend.bazaar.data.model;

import java.util.List;

/*
 * Data transfer object for user response information.
 */
public record UserResponseDto(Long id, Boolean enabled, UserRoleDto role, String email, String name,
                              String surnames, String address, String city, String province,
                              String zipCode, List<CategoryDto> favCategories) {
}
