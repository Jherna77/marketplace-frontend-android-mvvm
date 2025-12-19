package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.UserRequestDto;
import com.jhernandez.frontend.bazaar.data.model.UserResponseDto;
import com.jhernandez.frontend.bazaar.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between User DTOs and User domain model.
 */
public class UserMapper {

    private static final String PROTECTED = "[PROTECTED]";

    public static UserRequestDto toDto(User user) {
        return new UserRequestDto(user.id(), user.enabled(), UserRoleMapper.toDto(user.role()), user.email(), user.password(), user.name(), user.surnames(), user.address(), user.city(), user.province(), user.zipCode(), CategoryMapper.toDtoList(user.favCategories()));
    }

    public static User toDomain(UserResponseDto dto) {
        return new User(dto.id(), dto.enabled(), UserRoleMapper.toDomain(dto.role()), dto.email(), PROTECTED, dto.name(), dto.surnames(), dto.address(), dto.city(), dto.province(), dto.zipCode(), CategoryMapper.toDomainList(dto.favCategories()));
    }

    public static List<User> toDomainList(List<UserResponseDto> dtos) {
        return dtos.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}
