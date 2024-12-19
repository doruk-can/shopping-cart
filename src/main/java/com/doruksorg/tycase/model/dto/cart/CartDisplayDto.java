package com.doruksorg.tycase.model.dto.cart;

import com.doruksorg.tycase.model.dto.item.ItemDto;
import lombok.Data;

import java.util.List;

@Data
public class CartDisplayDto {

    private List<ItemDto> itemDtoList;
    private double totalPrice;
    private int appliedPromotionId;
    private double discountApplied;

}

