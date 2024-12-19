package com.doruksorg.tycase.unit.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.service.promotion.strategy.SameSellerPromotion;
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
public class SameSellerPromotionTests {

    @Mock
    private PromotionProperties promotionProperties;

    @InjectMocks
    private SameSellerPromotion sameSellerPromotion;

    @Test
    public void shouldApplySameSellerPromotion() {
        CartDto cartDto = CartDto.builder()
                .totalPrice(2000.0)
                .itemMapsContainer(
                        ItemMapsContainerDto.builder()
                                .defaultItemDtoMap(
                                        Map.of(
                                                "1", DefaultItemDto.builder()
                                                        .sellerId(300)
                                                        .price(500.0)
                                                        .quantity(2)
                                                        .build(),
                                                "2", DefaultItemDto.builder()
                                                        .sellerId(300)
                                                        .price(500.0)
                                                        .quantity(2)
                                                        .build()
                                        )
                                )
                                .build()
                )
                .build();

        PromotionProperties.Promotion mockSameSellerPromotion = mock(PromotionProperties.Promotion.class);
        when(promotionProperties.getSameSeller()).thenReturn(mockSameSellerPromotion);
        when(mockSameSellerPromotion.getDiscountRate()).thenReturn(0.1);

        double result = sameSellerPromotion.applyPromotion(cartDto);
        assertEquals(200.0, result);
    }

    @Test
    public void shouldNotApplySameSellerPromotionIfSellerIdsDifferent() {
        CartDto cartDto = CartDto.builder()
                .totalPrice(2000.0)
                .itemMapsContainer(
                        ItemMapsContainerDto.builder()
                                .defaultItemDtoMap(
                                        Map.of(
                                                "1", DefaultItemDto.builder()
                                                        .sellerId(200)
                                                        .price(500.0)
                                                        .quantity(2)
                                                        .build(),
                                                "2", DefaultItemDto.builder()
                                                        .sellerId(300)
                                                        .price(500.0)
                                                        .quantity(2)
                                                        .build()
                                        )
                                )
                                .build()
                )
                .build();

        double result = sameSellerPromotion.applyPromotion(cartDto);
        assertEquals(0.0, result);
    }


}
