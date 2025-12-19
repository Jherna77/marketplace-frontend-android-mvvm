package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.CategoryDto;
import com.jhernandez.frontend.bazaar.domain.model.Category;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between CategoryDto and Category domain model.
 */
public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.id(), category.enabled(), category.name(), category.imageUrl());
    }

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    public static Category toDomain(CategoryDto categoryDto) {
        return new Category(categoryDto.id(), categoryDto.enabled(), categoryDto.name(), categoryDto.imageUrl());
    }

    public static List<Category> toDomainList(List<CategoryDto> categoryDtos) {
        return categoryDtos.stream().map(CategoryMapper::toDomain).collect(Collectors.toList());
    }

}
