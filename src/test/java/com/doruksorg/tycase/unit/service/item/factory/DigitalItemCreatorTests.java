package com.doruksorg.tycase.unit.service.item.factory;

import com.doruksorg.tycase.exception.ExcessiveQuantityException;
import com.doruksorg.tycase.exception.InvalidItemCategoryException;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.service.item.factory.DigitalItemCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.doruksorg.tycase.util.Constants.CartValidations.MAX_QUANTITY_PER_DIGITAL_ITEM;
import static com.doruksorg.tycase.util.Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DigitalItemCreatorTests {

    @Mock
    private AddItemRequest request;

    @Mock
    private CartDto cartDto;

    private DigitalItemCreator digitalItemCreator;

    @BeforeEach
    public void setup() {
        digitalItemCreator = new DigitalItemCreator(request, cartDto);
    }

    @Test
    public void shouldCreateDigitalItem() {
        when(request.getCategoryId()).thenReturn(DIGITAL_ITEM_CATEGORY_ID);
        when(request.getQuantity()).thenReturn(MAX_QUANTITY_PER_DIGITAL_ITEM - 1);

        ItemDto itemDto = digitalItemCreator.createItem();

        assertNotNull(itemDto);
        assertEquals(DIGITAL_ITEM_CATEGORY_ID, itemDto.getCategoryId());
        assertEquals(MAX_QUANTITY_PER_DIGITAL_ITEM - 1, itemDto.getQuantity());
    }

    @Test
    public void shouldThrowExceptionOnExcessiveQuantity() {
        when(request.getCategoryId()).thenReturn(534232423);

        assertThrows(InvalidItemCategoryException.class, () -> digitalItemCreator.createItem());
    }

    @Test
    public void shouldThrowExceptionOnInvalidCategory() {
        when(request.getCategoryId()).thenReturn(DIGITAL_ITEM_CATEGORY_ID);
        when(request.getQuantity()).thenReturn(MAX_QUANTITY_PER_DIGITAL_ITEM + 1);

        assertThrows(ExcessiveQuantityException.class, () -> digitalItemCreator.createItem());
    }
}