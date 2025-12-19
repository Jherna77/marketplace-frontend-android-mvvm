package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.UserRoleDto;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between UserRoleDto and UserRole domain model.
 */
public class UserRoleMapper {

    public static UserRole toDomain (UserRoleDto dto) {
        return new UserRole(dto.id(), dto.name());
    }

    public static UserRoleDto toDto (UserRole domain) {
        return new UserRoleDto(domain.id(), domain.name());
    }

    public static List<UserRole> toDomainList (List<UserRoleDto> dtos) {
        return dtos.stream()
                .map(UserRoleMapper::toDomain)
                .collect(Collectors.toList());
    }
}
