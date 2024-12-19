package com.doruksorg.tycase.service.promotion.service;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.promotion.PromotionDetailsDto;

public interface PromotionService {

    PromotionDetailsDto applyBestPromotion(CartDto cartDto);

}
