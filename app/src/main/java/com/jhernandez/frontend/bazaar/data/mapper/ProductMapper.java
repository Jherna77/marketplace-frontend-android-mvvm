package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.ProductDto;
import com.jhernandez.frontend.bazaar.domain.model.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between ProductDto and Product domain model.
 */
public class ProductMapper {

    public static ProductDto toDto(Product product) {
        return new ProductDto(product.id(), product.enabled(), product.name(),
                product.description(), product.price(), product.shipping(),
                CategoryMapper.toDtoList(product.categories()), product.imagesUrl(),
                product.shopId(), product.sold(), product.rating(), product.ratingCount(),
                product.hasDiscount(), product.discountPrice(), product.stock());
    }

    public static Product toDomain(ProductDto productDto) {
        return new Product(productDto.id(), productDto.enabled(), productDto.name(),
                productDto.description(), productDto.price(), productDto.shipping(),
                CategoryMapper.toDomainList(productDto.categories()),
                productDto.imagesUrl(), productDto.shopId(), productDto.sold(),
                productDto.rating(), productDto.ratingCount(), productDto.hasDiscount(),
                productDto.discountPrice(), productDto.stock());
    }

    public static List<Product> toDomainList(List<ProductDto> productDtos) {
        return productDtos.stream().map(ProductMapper::toDomain).collect(Collectors.toList());
    }

}
