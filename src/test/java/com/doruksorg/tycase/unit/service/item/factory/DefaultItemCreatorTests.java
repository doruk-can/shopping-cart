package com.doruksorg.tycase.unit.service.item.factory;

import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.service.item.factory.DefaultItemCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultItemCreatorTests {

    @Mock
    private AddItemRequest request;

    private DefaultItemCreator defaultItemCreator;

    @BeforeEach
    public void setup() {
        defaultItemCreator = new DefaultItemCreator(request);
    }

    @Test
    public void shouldCreateDefaultItem() {
        when(request.getItemId()).thenReturn(1);
        when(request.getCategoryId()).thenReturn(1);
        when(request.getSellerId()).thenReturn(1);
        when(request.getPrice()).thenReturn(100.0);
        when(request.getQuantity()).thenReturn(1);

        ItemDto itemDto = defaultItemCreator.createItem();

        assertNotNull(itemDto);
        assertTrue(itemDto instanceof DefaultItemDto);
        assertEquals(1L, itemDto.getItemId());
        assertEquals(1L, itemDto.getCategoryId());
        assertEquals(1L, itemDto.getSellerId());
        assertEquals(100.0, itemDto.getPrice());
        assertEquals(1, itemDto.getQuantity());
    }
}