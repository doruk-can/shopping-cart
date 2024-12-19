package com.doruksorg.tycase.model.dto.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DefaultItemDto extends ItemDto {
    private Set<String> subItemIdSet;
}

