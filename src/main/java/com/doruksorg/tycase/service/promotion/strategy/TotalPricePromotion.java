package com.doruksorg.tycase.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.enums.DiscountType;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.util.Constants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class TotalPricePromotion implements PromotionStrategy {

    private final PromotionProperties promotionProperties;

    @Override
    public double applyPromotion(CartDto cartDto) {
        double totalPrice = cartDto.getTotalPrice();
        PromotionProperties.Discount discount;

        if (totalPrice < promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT1.name()).getThreshold()) {
            return 0;
        } else if (totalPrice < promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT2.name()).getThreshold()) {
            discount = promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT1.name());
        } else if (totalPrice < promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT3.name()).getThreshold()) {
            discount = promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT2.name());
        } else if (totalPrice < promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT4.name()).getThreshold()) {
            discount = promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT3.name());
        } else {
            discount = promotionProperties.getTotalPrice().getDiscounts().get(DiscountType.DISCOUNT4.name());
        }

        return discount.getAmount();
    }


    @Override
    public Integer getPromotionId() {
        return Constants.PromotionIds.TOTAL_PRICE_PROMOTION_ID;
    }
}
