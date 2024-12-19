package com.doruksorg.tycase.unit.service.item.service;

import com.doruksorg.tycase.exception.ExcessiveQuantityException;
import com.doruksorg.tycase.exception.IncompatibleProductException;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.enums.CartType;
import com.doruksorg.tycase.model.enums.ItemType;
import com.doruksorg.tycase.service.item.service.ItemValidationServiceImpl;
import com.doruksorg.tycase.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ItemValidationServiceImplTests {

    private ItemValidationServiceImpl itemValidationService;

    @BeforeEach
    public void setup() {
        itemValidationService = new ItemValidationServiceImpl();
    }

    @Test
    public void shouldNotThrowExceptionWhenCartIsValid() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("123", new DefaultItemDto())));

        Assertions.assertDoesNotThrow(() -> itemValidationService.validateItemAddition(cartDto));
    }

    @Test
    public void shouldThrowExceptionWhenCartHasTooManyUniqueItems() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        HashMap<String, DefaultItemDto> items = new HashMap<>();
        for (int i = 0; i < Constants.CartValidations.MAX_UNIQUE_ITEMS_IN_CART + 1; i++) {
            items.put(String.valueOf(i), new DefaultItemDto());
        }
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(items);

        Assertions.assertThrows(ExcessiveQuantityException.class, () -> itemValidationService.validateItemAddition(cartDto));
    }

    @Test
    public void shouldThrowExceptionWhenCartHasTooManyTotalItems() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        DefaultItemDto item = new DefaultItemDto();
        item.setQuantity(Constants.CartValidations.MAX_TOTAL_QUANTITY_IN_CART + 1);
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("123", item)));

        Assertions.assertThrows(ExcessiveQuantityException.class, () -> itemValidationService.validateItemAddition(cartDto));
    }

    @Test
    public void shouldThrowExceptionWhenCartContainsIncompatibleProductTypes() {
        CartDto cartDto = new CartDto();
        cartDto.setCartType(CartType.DEFAULT);
        ItemDto itemDto = new DefaultItemDto();
        itemDto.setItemType(ItemType.DIGITAL);

        Assertions.assertThrows(IncompatibleProductException.class, () -> itemValidationService.validateItemCompatibility(cartDto, itemDto));
    }

    @Test
    public void shouldNotThrowExceptionWhenCartContainsCompatibleProductTypes() {
        CartDto cartDto = new CartDto();
        cartDto.setCartType(CartType.DEFAULT);
        ItemDto itemDto = new DefaultItemDto();
        itemDto.setItemType(ItemType.DEFAULT);

        Assertions.assertDoesNotThrow(() -> itemValidationService.validateItemCompatibility(cartDto, itemDto));
    }

    @Test
    public void shouldThrowExceptionWhenVasItemQuantityExceedsMax() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        VasItemDto vasItemDto = new VasItemDto();
        vasItemDto.setQuantity(Constants.CartValidations.MAX_VAS_ITEMS_IN_CART + 1);
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of("123", vasItemDto)));

        Assertions.assertThrows(ExcessiveQuantityException.class, () -> itemValidationService.validateVasItemQuantity(cartDto, vasItemDto));
    }

    @Test
    public void shouldNotThrowExceptionWhenVasItemQuantityIsWithinMax() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        VasItemDto vasItemDto = new VasItemDto();
        vasItemDto.setQuantity(1);
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of("123", vasItemDto)));

        Assertions.assertDoesNotThrow(() -> itemValidationService.validateVasItemQuantity(cartDto, vasItemDto));
    }

    @Test
    public void shouldThrowExceptionWhenVasItemIsNotAddedToDefaultItem() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        DefaultItemDto defaultItemDto = new DefaultItemDto();
        defaultItemDto.setCategoryId(666);
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("123", defaultItemDto)));
        VasItemDto vasItemDto = new VasItemDto();
        vasItemDto.setParentId(222);

        Assertions.assertThrows(IncompatibleProductException.class, () -> itemValidationService.validateVasItemCompatibility(cartDto, vasItemDto));
    }

    @Test
    public void shouldThrowExceptionWhenVasItemIsAddedToIncompatibleDefaultItem() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        DefaultItemDto defaultItemDto = new DefaultItemDto();
        defaultItemDto.setCategoryId(12223333);
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("12223333", defaultItemDto)));
        VasItemDto vasItemDto = new VasItemDto();
        vasItemDto.setParentId(12223333);

        Assertions.assertThrows(IncompatibleProductException.class, () -> itemValidationService.validateVasItemCompatibility(cartDto, vasItemDto));
    }

    @Test
    public void shouldNotThrowExceptionWhenVasItemIsAddedToCompatibleDefaultItem() {
        CartDto cartDto = new CartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        DefaultItemDto defaultItemDto = new DefaultItemDto();
        defaultItemDto.setCategoryId(Constants.CategoryIds.FURNITURE_ID);
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of(String.valueOf(Constants.CategoryIds.FURNITURE_ID), defaultItemDto)));
        VasItemDto vasItemDto = new VasItemDto();
        vasItemDto.setParentId(Constants.CategoryIds.FURNITURE_ID);

        Assertions.assertDoesNotThrow(() -> itemValidationService.validateVasItemCompatibility(cartDto, vasItemDto));
    }

    @Test
    public void shouldThrowExceptionWhenTotalCartPriceExceedsMax() {
        double totalPrice = Constants.CartValidations.MAX_TOTAL_CART_PRICE + 1;
        Assertions.assertThrows(ExcessiveQuantityException.class, () -> itemValidationService.validateTotalCartPrice(totalPrice));
    }

    @Test
    public void shouldNotThrowExceptionWhenTotalCartPriceIsWithinMax() {
        double totalPrice = Constants.CartValidations.MAX_TOTAL_CART_PRICE - 1;
        Assertions.assertDoesNotThrow(() -> itemValidationService.validateTotalCartPrice(totalPrice));
    }

}
