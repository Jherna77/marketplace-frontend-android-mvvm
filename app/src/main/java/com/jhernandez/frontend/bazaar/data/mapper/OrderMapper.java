package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.OrderDto;
import com.jhernandez.frontend.bazaar.domain.model.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between OrderDto and Order domain model.
 */
public class OrderMapper {

    public static OrderDto toDto(Order order) {
        return new OrderDto(order.id(), order.status(), ItemMapper.toDto(order.item()), order.customerId(), order.shopId(), order.orderDate());
    }

    public static Order toDomain(OrderDto orderDto) {
        return new Order(orderDto.id(), orderDto.status(), ItemMapper.toDomain(orderDto.item()), orderDto.customerId(), orderDto.shopId(), orderDto.orderDate());
    }

    public static List<Order> toDomainList(List<OrderDto> orderDtoList) {
        return orderDtoList.stream().map(OrderMapper::toDomain).collect(Collectors.toList());
    }
}
