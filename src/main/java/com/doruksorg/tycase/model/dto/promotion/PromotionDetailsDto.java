package com.doruksorg.tycase.model.dto.promotion;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PromotionDetailsDto {

    private double appliedDiscount;
    private int appliedPromotionId;

}

