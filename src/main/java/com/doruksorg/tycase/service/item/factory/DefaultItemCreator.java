package com.doruksorg.tycase.service.item.factory;

import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.enums.ItemType;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultItemCreator implements ItemCreator {

    private final AddItemRequest request;

    @Override
    public ItemDto createItem() {
        return DefaultItemDto.builder()
                .itemId(request.getItemId())
                .categoryId(request.getCategoryId())
                .sellerId(request.getSellerId())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .itemType(ItemType.DEFAULT)
                .build();
    }
}
