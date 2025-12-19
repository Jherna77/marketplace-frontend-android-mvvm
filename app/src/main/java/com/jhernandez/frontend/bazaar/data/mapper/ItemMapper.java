package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.ItemDto;
import com.jhernandez.frontend.bazaar.domain.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between ItemDto and Item domain model.
 */
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), ProductMapper.toDto(item.getProduct()), item.getSalePrice(), item.getSaleShipping(), item.getQuantity(), item.getTotalPrice());
    }

    public static Item toDomain(ItemDto itemDto) {
        return new Item(itemDto.id(), ProductMapper.toDomain(itemDto.product()), itemDto.salePrice(), itemDto.saleShipping(), itemDto.quantity(), itemDto.totalPrice());
    }

    public static List<ItemDto> toDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    public static List<Item> toDomainList(List<ItemDto> itemDtos) {
        return itemDtos.stream().map(ItemMapper::toDomain).collect(Collectors.toList());
    }
}
