package com.doruksorg.tycase.unit.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.enums.DiscountType;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.service.promotion.strategy.TotalPricePromotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TotalPricePromotionTests {

    @Mock
    private PromotionProperties promotionProperties;

    @InjectMocks
    private TotalPricePromotion totalPricePromotion;

    @BeforeEach
    public void setup() {
        PromotionProperties.Discount discount1 = new PromotionProperties.Discount();
        discount1.setAmount(250.0);
        discount1.setThreshold(500.0);

        PromotionProperties.Discount discount2 = new PromotionProperties.Discount();
        discount2.setAmount(500.0);
        discount2.setThreshold(5000.0);

        PromotionProperties.Discount discount3 = new PromotionProperties.Discount();
        discount3.setAmount(1000.0);
        discount3.setThreshold(1000.0);

        PromotionProperties.Discount discount4 = new PromotionProperties.Discount();
        discount4.setAmount(2000.0);
        discount4.setThreshold(50000.0);

        PromotionProperties.TotalPricePromotion mockTotalPricePromotion = mock(PromotionProperties.TotalPricePromotion.class);
        when(promotionProperties.getTotalPrice()).thenReturn(mockTotalPricePromotion);
        when(mockTotalPricePromotion.getDiscounts())
                .thenReturn(Map.of(DiscountType.DISCOUNT1.name(), discount1, DiscountType.DISCOUNT2.name(), discount2, DiscountType.DISCOUNT3.name(), discount3, DiscountType.DISCOUNT4.name(), discount4));
    }

    @Test
    public void shouldApplyLowestTotalPricePromotion() {
        CartDto cartDto = CartDto.builder()
                .totalPrice(1000.0)
                .build();

        double result = totalPricePromotion.applyPromotion(cartDto);
        assertEquals(250.0, result);
    }

    @Test
    public void shouldApplyHighestTotalPricePromotion() {
        CartDto cartDto = CartDto.builder()
                .totalPrice(51000.0)
                .build();

        double result = totalPricePromotion.applyPromotion(cartDto);
        assertEquals(2000.0, result);
    }

    @Test
    public void shouldNotApplyTotalPricePromotionDueToMinThreshold() {
        CartDto cartDto = CartDto.builder()
                .totalPrice(100.0)
                .build();

        double result = totalPricePromotion.applyPromotion(cartDto);
        assertEquals(0.0, result);
    }

}
