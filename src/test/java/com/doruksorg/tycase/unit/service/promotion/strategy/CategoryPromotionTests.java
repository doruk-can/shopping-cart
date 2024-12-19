package com.doruksorg.tycase.unit.service.promotion.strategy;

import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.service.promotion.configuration.PromotionProperties;
import com.doruksorg.tycase.service.promotion.strategy.CategoryPromotion;
import com.doruksorg.tycase.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryPromotionTests {

    @Mock
    private PromotionProperties promotionProperties;

    @InjectMocks
    private CategoryPromotion categoryPromotion;



    @Test
    public void shouldReturnZeroWhileCartIsNull() {
        CartDto cartDto = new CartDto();

        double v = categoryPromotion.applyPromotion(cartDto);
        assertEquals(0, v);
    }

    @Test
    public void shouldApplyCategoryPromotion() {

        PromotionProperties.Promotion mockPromotionCategory = mock(PromotionProperties.Promotion.class);
        when(promotionProperties.getCategory()).thenReturn(mockPromotionCategory);
        when(mockPromotionCategory.getDiscountRate()).thenReturn(0.05);

        CartDto cartDto = CartDto.builder()
                .totalPrice(1000.0)
                .itemMapsContainer(
                        ItemMapsContainerDto.builder()
                                .defaultItemDtoMap(
                                        Map.of("1", DefaultItemDto.builder()
                                                .categoryId(Constants.CategoryIds.DISCOUNTED_CATEGORY_ID)
                                                .price(500.0)
                                                .quantity(2)
                                                .build())
                                )
                                .build()
                )
                .build();

        double result = categoryPromotion.applyPromotion(cartDto);
        assertEquals(50.0, result);
    }

    @Test
    public void shouldNotApplyCategoryPromotionIfCategoryIdNotDiscountedCategoryId() {

        PromotionProperties.Promotion mockPromotionCategory = mock(PromotionProperties.Promotion.class);
        when(promotionProperties.getCategory()).thenReturn(mockPromotionCategory);
        when(mockPromotionCategory.getDiscountRate()).thenReturn(0.05);

        CartDto cartDto = CartDto.builder()
                .totalPrice(1000.0)
                .itemMapsContainer(
                        ItemMapsContainerDto.builder()
                                .defaultItemDtoMap(
                                        Map.of("1", DefaultItemDto.builder()
                                                .categoryId(143242)
                                                .price(500.0)
                                                .quantity(2)
                                                .build())
                                )
                                .build()
                )
                .build();

        double result = categoryPromotion.applyPromotion(cartDto);
        assertEquals(0.0, result);
    }


}
