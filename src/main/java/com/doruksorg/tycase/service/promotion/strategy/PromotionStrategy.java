package com.doruksorg.tycase.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;

public interface PromotionStrategy {

    double applyPromotion(CartDto cartDto);

    Integer getPromotionId();

}
