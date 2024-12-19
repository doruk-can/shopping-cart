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
public class AddItemRequest {

    @NotNull
    private Integer itemId;
    @NotNull
    private Integer categoryId;
    @NotNull
    private Integer sellerId;
    @NotNull
    private Double price;
    @NotNull
    private Integer quantity;

}
