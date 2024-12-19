package com.doruksorg.tycase.service.item.service;


import com.doruksorg.tycase.exception.ExcessiveQuantityException;
import com.doruksorg.tycase.exception.IncompatibleProductException;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.enums.CartType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.doruksorg.tycase.util.Constants.CartValidations.*;

@Slf4j
@Service
public class ItemValidationServiceImpl implements ItemValidationService {

    @Override
    public void validateItemAddition(CartDto cartDto) {
        log.info("Validating item addition for cart ID: {}", cartDto.getId());
        if (cartDto.getItemMapsContainer() == null) {
            log.warn("ItemMapsContainer is null for cart ID: {}", cartDto.getId());
            return;
        }

        ItemMapsContainerDto itemMapsContainer = cartDto.getItemMapsContainer();

        int totalUniqueItemsInCart = calculateTotalUniqueItems(itemMapsContainer);
        log.info("Total unique items in cart: {}", totalUniqueItemsInCart);
        if (totalUniqueItemsInCart > MAX_UNIQUE_ITEMS_IN_CART) {
            log.warn("Unique item quantity exceeds the maximum allowed.");
            throw new ExcessiveQuantityException("Unique item quantity exceeds the maximum allowed.");
        }

        int totalItemQuantity = calculateTotalItemQuantity(itemMapsContainer);
        log.info("Total item quantity in cart: {}", totalItemQuantity);
        if (totalItemQuantity > MAX_TOTAL_QUANTITY_IN_CART) {
            log.warn("Total item quantity exceeds the maximum allowed.");
            throw new ExcessiveQuantityException("Total item quantity exceeds the maximum allowed.");
        }
    }

    @Override
    public void validateItemCompatibility(CartDto cartDto, ItemDto itemDto) {
        log.info("Validating item compatibility. Cart type: {}, Item type: {}", cartDto.getCartType(), itemDto.getItemType());
        if (cartDto.getCartType() != null && !cartDto.getCartType().equals(CartType.fromItemType(itemDto.getItemType()))) {
            log.warn("Incompatible product types. Cart type: {}, Item type: {}", cartDto.getCartType(), itemDto.getItemType());
            throw new IncompatibleProductException("Cart contains incompatible product types.");
        }
    }

    @Override
    public void validateVasItemQuantity(CartDto cartDto, VasItemDto vasItemDto) {
        int totalVasItems = 0;
        if (cartDto.getItemMapsContainer() != null && cartDto.getItemMapsContainer().getVasItemDtoMap() != null) {
            totalVasItems = cartDto.getItemMapsContainer().getVasItemDtoMap().values().stream().mapToInt(ItemDto::getQuantity).sum();
        }
        log.info("Total VasItems in cart: {}. Quantity of VasItem being added: {}", totalVasItems, vasItemDto.getQuantity());


        if (totalVasItems + vasItemDto.getQuantity() > MAX_VAS_ITEMS_IN_CART) {
            log.warn("VasItem quantity exceeds the maximum allowed.");
            throw new ExcessiveQuantityException("VasItem quantity exceeds the maximum allowed.");
        }
    }

    @Override
    public void validateVasItemCompatibility(CartDto cartDto, VasItemDto vasItemDto) {
        log.info("Validating VasItem compatibility. Parent ID of VasItem: {}", vasItemDto.getParentId());
        DefaultItemDto defaultItemDto = cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(vasItemDto.getParentId()));
        if (defaultItemDto == null) {
            log.warn("Default item not found for item ID: {}", vasItemDto.getParentId());
            throw new IncompatibleProductException("VasItem must be added to default item.");
        } else if (!VAS_ITEM_COMPATIBLE_CATEGORY_ID_LIST.contains(defaultItemDto.getCategoryId())) {
            log.warn("Incompatible category ID. Default item category ID: {}", defaultItemDto.getCategoryId());
            throw new IncompatibleProductException("VasItem must be added to default item with compatible category ID.");
        }
    }

    @Override
    public void validateTotalCartPrice(double totalPrice) {
        log.info("Validating total cart price. Total price: {}", totalPrice);
        if (totalPrice > MAX_TOTAL_CART_PRICE) {
            log.warn("Total cart price exceeds the maximum allowed. Total price: {}", totalPrice);
            throw new ExcessiveQuantityException("Total cart price exceeds the maximum allowed.");
        }
    }

    private int calculateTotalUniqueItems(ItemMapsContainerDto itemMapsContainer) {
        int totalUniqueItems = 0;
        totalUniqueItems += getNonNullMapSize(itemMapsContainer.getDefaultItemDtoMap());
        totalUniqueItems += getNonNullMapSize(itemMapsContainer.getDigitalItemDtoMap());
        totalUniqueItems += getNonNullMapSize(itemMapsContainer.getVasItemDtoMap());
        return totalUniqueItems;
    }

    private int calculateTotalItemQuantity(ItemMapsContainerDto itemMapsContainer) {
        int totalQuantity = 0;
        totalQuantity += getNonNullMapQuantitySum(itemMapsContainer.getDefaultItemDtoMap());
        totalQuantity += getNonNullMapQuantitySum(itemMapsContainer.getDigitalItemDtoMap());
        totalQuantity += getNonNullMapQuantitySum(itemMapsContainer.getVasItemDtoMap());
        return totalQuantity;
    }

    private int getNonNullMapSize(Map<String, ? extends ItemDto> itemDtoMap) {
        return (itemDtoMap != null) ? itemDtoMap.size() : 0;
    }

    private int getNonNullMapQuantitySum(Map<String, ? extends ItemDto> itemDtoMap) {
        return (itemDtoMap != null) ? itemDtoMap.values().stream().mapToInt(ItemDto::getQuantity).sum() : 0;
    }

}
