package com.doruksorg.tycase.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.util.Constants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class SameSellerPromotion implements PromotionStrategy {

    private final PromotionProperties promotionProperties;

    @Override
    public double applyPromotion(CartDto cartDto) {
        if (cartDto == null || cartDto.getItemMapsContainer() == null || cartDto.getItemMapsContainer().getDefaultItemDtoMap() == null) {
            return 0;
        }

        if (cartDto.getItemMapsContainer().getDefaultItemDtoMap().values().stream()
                .map(ItemDto::getSellerId)
                .distinct()
                .count() == 1) {
            if (promotionProperties.getSameSeller() != null) {
                return cartDto.getTotalPrice() * promotionProperties.getSameSeller().getDiscountRate();
            }
        }
        return 0;
    }

    @Override
    public Integer getPromotionId() {
        return Constants.PromotionIds.SAME_SELLER_PROMOTION_ID;
    }
}
