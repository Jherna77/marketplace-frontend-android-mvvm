package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.ReviewDto;
import com.jhernandez.frontend.bazaar.domain.model.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between ReviewDto and Review domain model.
 */
public class ReviewMapper {

    public static ReviewDto toDto(Review review) {
        return new ReviewDto(review.id(), review.author(), review.orderId(), review.comment(), review.rating(), review.reviewDate());
    }

    public static Review toDomain(ReviewDto dto) {
        return new Review(dto.id(), dto.author(), dto.orderId(), dto.comment(), dto.rating(), dto.reviewDate());
    }

    public static List<Review> toDomainList(List<ReviewDto> dtos) {
        return dtos.stream()
                .map(ReviewMapper::toDomain)
                .collect(Collectors.toList());
    }


}
