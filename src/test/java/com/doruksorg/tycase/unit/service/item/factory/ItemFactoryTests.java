package com.doruksorg.tycase.unit.service.item.factory;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.service.item.factory.ItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.doruksorg.tycase.util.Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID;
import static com.doruksorg.tycase.util.Constants.CategoryIds.VAS_ITEM_CATEGORY_ID;
import static com.doruksorg.tycase.util.Constants.SellerIds.VAS_ITEM_SELLER_ID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemFactoryTests {

    @Mock
    private AddItemRequest addItemRequest;

    @Mock
    private AddVasItemRequest addVasItemRequest;

    @Mock
    private CartDto cartDto;


    @Test
    public void shouldCreateDefaultItem() {
        ItemDto itemDto = ItemFactory.createItem(addItemRequest, cartDto);

        assertNotNull(itemDto);
        assertTrue(itemDto instanceof DefaultItemDto);
    }

    @Test
    public void shouldCreateDigitalItem() {
        when(addItemRequest.getCategoryId()).thenReturn(DIGITAL_ITEM_CATEGORY_ID);

        ItemDto itemDto = ItemFactory.createItem(addItemRequest, cartDto);

        assertNotNull(itemDto);
        assertTrue(itemDto instanceof DigitalItemDto);
    }

    @Test
    public void shouldCreateVasItem() {
        when(addVasItemRequest.getVasCategoryId()).thenReturn(VAS_ITEM_CATEGORY_ID);
        when(addVasItemRequest.getVasSellerId()).thenReturn(VAS_ITEM_SELLER_ID);

        ItemDto itemDto = ItemFactory.createItem(addVasItemRequest);

        assertNotNull(itemDto);
        assertTrue(itemDto instanceof VasItemDto);
    }


}