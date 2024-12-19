package com.doruksorg.tycase.service.item.factory;

import com.doruksorg.tycase.exception.ExcessiveQuantityException;
import com.doruksorg.tycase.exception.InvalidItemCategoryException;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.enums.ItemType;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.doruksorg.tycase.util.Constants.CartValidations.MAX_QUANTITY_PER_DIGITAL_ITEM;
import static com.doruksorg.tycase.util.Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID;

@Slf4j
@RequiredArgsConstructor
public class DigitalItemCreator implements ItemCreator {

    private final AddItemRequest request;
    private final CartDto cartDto;


    @Override
    public ItemDto createItem() {

        DigitalItemDto digitalItemDto = DigitalItemDto.builder()
                .itemId(request.getItemId())
                .categoryId(request.getCategoryId())
                .sellerId(request.getSellerId())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .itemType(ItemType.DIGITAL)
                .build();

        checkDigitalCategory();
        validateDigitalItemQuantity(digitalItemDto);

        return digitalItemDto;
    }

    private void checkDigitalCategory() {
        log.info("Checking category ID: {}", request.getCategoryId());
        if (request.getCategoryId() != DIGITAL_ITEM_CATEGORY_ID) {
            log.warn("Invalid category ID for DigitalItem. Expected: {}, but was: {}", DIGITAL_ITEM_CATEGORY_ID, request.getCategoryId());
            throw new InvalidItemCategoryException("DigitalItem must have the correct category ID.");
        }
    }

    private void validateDigitalItemQuantity(ItemDto itemDto) {
        log.info("Validating digital item quantity for item ID: {}", itemDto.getItemId());
        int currentDigitalItemCount = 0;
        if (cartDto.getItemMapsContainer() != null && cartDto.getItemMapsContainer().getDigitalItemDtoMap() != null) {
            currentDigitalItemCount = cartDto.getItemMapsContainer().getDigitalItemDtoMap().values().stream().mapToInt(ItemDto::getQuantity).sum();
            log.info("Current digital item count: {}", currentDigitalItemCount);
        }
        if (currentDigitalItemCount + itemDto.getQuantity() > MAX_QUANTITY_PER_DIGITAL_ITEM) {
            log.warn("DigitalItem quantity exceeds the maximum allowed. Current quantity: {}, Item quantity: {}, Maximum allowed: {}", currentDigitalItemCount, itemDto.getQuantity(), MAX_QUANTITY_PER_DIGITAL_ITEM);
            throw new ExcessiveQuantityException("DigitalItem quantity exceeds the maximum allowed.");
        }
    }
}
