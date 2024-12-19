package com.doruksorg.tycase.service.promotion.service;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.promotion.PromotionDetailsDto;
import com.doruksorg.tycase.service.promotion.strategy.PromotionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final List<PromotionStrategy> promotionStrategyList;

    @Override
    public PromotionDetailsDto applyBestPromotion(CartDto cartDto) {
        PromotionDetailsDto bestPromotion = promotionStrategyList.stream()
                .map(p -> PromotionDetailsDto.builder()
                        .appliedDiscount(p.applyPromotion(cartDto))
                        .appliedPromotionId(p.getPromotionId())
                        .build())
                .max(Comparator.comparing(PromotionDetailsDto::getAppliedDiscount))
                .orElse(null);

        if (bestPromotion == null || bestPromotion.getAppliedDiscount() == 0) {
            return PromotionDetailsDto.builder()
                    .appliedDiscount(0)
                    .appliedPromotionId(-1)
                    .build();
        }

        return bestPromotion;
    }

}
