package com.doruksorg.tycase.service.item.factory;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.util.Constants;

public class ItemFactory {

    public static ItemDto createItem(AddItemRequest request, CartDto cartDto) {
        ItemCreator creator;
        switch (request.getCategoryId()) {
            case Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID:
                creator = new DigitalItemCreator(request, cartDto);
                break;
            default:
                creator = new DefaultItemCreator(request);
                break;
        }
        return creator.createItem();
    }

    public static ItemDto createItem(AddVasItemRequest request) {
        ItemCreator creator = new VasItemCreator(request);
        return creator.createItem();
    }

}
