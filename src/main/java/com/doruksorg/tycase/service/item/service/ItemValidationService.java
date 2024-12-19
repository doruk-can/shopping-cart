package com.doruksorg.tycase.service.item.service;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;

public interface ItemValidationService {

    void validateItemAddition(CartDto cartDto);

    void validateItemCompatibility(CartDto cartDto, ItemDto itemDto);

    void validateVasItemQuantity(CartDto cartDto, VasItemDto vasItemDto);

    void validateVasItemCompatibility(CartDto cartDto, VasItemDto vasItemDto);

    void validateTotalCartPrice(double totalPrice);

}
