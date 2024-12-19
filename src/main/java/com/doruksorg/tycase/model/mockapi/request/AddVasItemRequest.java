package com.doruksorg.tycase.model.mockapi.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddVasItemRequest {

    @NotNull
    private Integer itemId;
    @NotNull
    private Integer vasItemId;
    @NotNull
    private Integer vasCategoryId;
    @NotNull
    private Integer vasSellerId;
    @NotNull
    private Double price;
    @NotNull
    private Integer quantity;
}
