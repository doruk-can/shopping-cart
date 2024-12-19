package com.doruksorg.tycase.model.dto.item;

import com.doruksorg.tycase.model.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class ItemDto {
    protected int itemId;
    protected int categoryId;
    protected int sellerId;
    protected double price;
    protected int quantity;
    private ItemType itemType;
}

