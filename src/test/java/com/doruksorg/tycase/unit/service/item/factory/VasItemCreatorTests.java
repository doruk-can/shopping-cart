package com.doruksorg.tycase.unit.service.item.factory;

import com.doruksorg.tycase.exception.InvalidItemCategoryException;
import com.doruksorg.tycase.exception.InvalidSellerIdException;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.service.item.factory.VasItemCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.doruksorg.tycase.util.Constants.CategoryIds.VAS_ITEM_CATEGORY_ID;
import static com.doruksorg.tycase.util.Constants.SellerIds.VAS_ITEM_SELLER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VasItemCreatorTests {

    @Mock
    private AddVasItemRequest request;

    private VasItemCreator vasItemCreator;

    @BeforeEach
    public void setup() {
        vasItemCreator = new VasItemCreator(request);
    }

    @Test
    public void shouldCreateVasItem() {
        when(request.getVasCategoryId()).thenReturn(VAS_ITEM_CATEGORY_ID);
        when(request.getVasSellerId()).thenReturn(VAS_ITEM_SELLER_ID);

        ItemDto itemDto = vasItemCreator.createItem();

        assertNotNull(itemDto);
        assertEquals(VAS_ITEM_CATEGORY_ID, itemDto.getCategoryId());
        assertEquals(VAS_ITEM_SELLER_ID, itemDto.getSellerId());
    }

    @Test
    public void shouldThrowExceptionOnInvalidCategory() {
        when(request.getVasCategoryId()).thenReturn(3242532);

        assertThrows(InvalidItemCategoryException.class, () -> vasItemCreator.createItem());
    }

    @Test
    public void shouldThrowExceptionOnInvalidSeller() {
        when(request.getVasCategoryId()).thenReturn(VAS_ITEM_CATEGORY_ID);
        when(request.getVasSellerId()).thenReturn(2342523);

        assertThrows(InvalidSellerIdException.class, () -> vasItemCreator.createItem());
    }
}