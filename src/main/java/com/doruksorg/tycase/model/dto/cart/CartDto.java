package com.doruksorg.tycase.model.dto.cart;

import com.doruksorg.tycase.model.enums.CartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private String id;
    private String userId;
    private ItemMapsContainerDto itemMapsContainer;
    private double totalPrice;
    private double discountApplied;
    private double finalPrice;
    private CartType cartType;
    private int appliedPromotionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

    public void reset() {
        this.setItemMapsContainer(null);
        this.setFinalPrice(0);
        this.setDiscountApplied(0);
        this.setTotalPrice(0);
        this.setCartType(null);
        this.setAppliedPromotionId(-1);
    }
}

