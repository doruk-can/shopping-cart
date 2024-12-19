package com.doruksorg.tycase.unit.service.promotion.service;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.promotion.PromotionDetailsDto;
import com.doruksorg.tycase.service.promotion.service.PromotionServiceImpl;
import com.doruksorg.tycase.service.promotion.strategy.PromotionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceImplTests {

    @Mock
    private PromotionStrategy sameSellerPromotion;

    @Mock
    private PromotionStrategy categoryPromotion;

    @Mock
    private PromotionStrategy totalPricePromotion;

    private PromotionServiceImpl promotionService;

    @BeforeEach
    public void setup() {
        List<PromotionStrategy> promotionStrategyList = Arrays.asList(sameSellerPromotion, categoryPromotion, totalPricePromotion);
        promotionService = new PromotionServiceImpl(promotionStrategyList);
    }

    @Test
    public void shouldApplyBestPromotion_SameSellerPromotion() {
        CartDto cartDto = new CartDto();
        when(sameSellerPromotion.applyPromotion(cartDto)).thenReturn(100.0);
        when(sameSellerPromotion.getPromotionId()).thenReturn(9909);
        when(categoryPromotion.applyPromotion(cartDto)).thenReturn(50.0);
        when(totalPricePromotion.applyPromotion(cartDto)).thenReturn(75.0);

        PromotionDetailsDto result = promotionService.applyBestPromotion(cartDto);

        assertEquals(9909, result.getAppliedPromotionId());
        assertEquals(100.0, result.getAppliedDiscount());
    }

}
