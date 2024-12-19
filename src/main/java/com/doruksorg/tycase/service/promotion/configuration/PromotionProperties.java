package com.doruksorg.tycase.service.promotion.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "promotion")
public class PromotionProperties {

    private Promotion sameSeller;
    private Promotion category;
    private TotalPricePromotion totalPrice;

    @Data
    public static class Promotion {
        private double discountRate;
    }

    @Data
    public static class TotalPricePromotion {
        private Map<String, Discount> discounts;
    }

    @Data
    public static class Discount {
        private double threshold;
        private double amount;
    }
}