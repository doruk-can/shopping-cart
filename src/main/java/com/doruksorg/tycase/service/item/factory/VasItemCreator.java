package com.doruksorg.tycase.service.item.factory;

import com.doruksorg.tycase.exception.InvalidItemCategoryException;
import com.doruksorg.tycase.exception.InvalidSellerIdException;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.enums.ItemType;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class VasItemCreator implements ItemCreator {

    private final AddVasItemRequest request;

    @Override
    public ItemDto createItem() {

        validateCategory(request.getVasCategoryId());
        validateSeller(request.getVasSellerId());

        return VasItemDto.builder()
                .parentId(request.getItemId())
                .itemId(request.getVasItemId())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .categoryId(request.getVasCategoryId())
                .sellerId(request.getVasSellerId())
                .itemType(ItemType.VAS)
                .build();
    }

    private void validateCategory(int categoryId) {
        log.info("Validating category ID: {}", categoryId);
        if (Constants.CategoryIds.VAS_ITEM_CATEGORY_ID != categoryId) {
            log.warn("Invalid category ID for VasItem. Expected: {}, but was: {}", Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, categoryId);
            throw new InvalidItemCategoryException("VasItem must have the correct category ID.");
        }
    }

    private void validateSeller(int sellerId) {
        log.info("Validating seller ID: {}", sellerId);
        if (Constants.SellerIds.VAS_ITEM_SELLER_ID != sellerId) {
            log.warn("Invalid seller ID for VasItem. Expected: {}, but was: {}", Constants.SellerIds.VAS_ITEM_SELLER_ID, sellerId);
            throw new InvalidSellerIdException("VasItem must have the correct seller ID.");
        }
    }


}