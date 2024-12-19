package com.doruksorg.tycase.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryPromotion implements PromotionStrategy {

    private final PromotionProperties promotionProperties;

    @Override
    public double applyPromotion(CartDto cartDto) {
        if (cartDto == null || cartDto.getItemMapsContainer() == null) {
            return 0;
        }

        // The digital item category has a unique category ID, and a cart cannot contain other types of items simultaneously.
        if (cartDto.getItemMapsContainer().getDigitalItemDtoMap() != null
                && !cartDto.getItemMapsContainer().getDigitalItemDtoMap().isEmpty()) {
            return cartDto.getTotalPrice() * promotionProperties.getCategory().getDiscountRate();
        }


        if (cartDto.getItemMapsContainer().getDefaultItemDtoMap() != null) {
            return cartDto.getItemMapsContainer().getDefaultItemDtoMap().values().stream()
                    .filter(item -> item.getCategoryId() == Constants.CategoryIds.DISCOUNTED_CATEGORY_ID)
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum() * promotionProperties.getCategory().getDiscountRate();
        }

        return 0;
    }

    @Override
    public Integer getPromotionId() {
        return Constants.PromotionIds.CATEGORY_PROMOTION_ID;
    }
}
