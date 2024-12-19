package com.doruksorg.tycase.model.dto.cart;

import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemMapsContainerDto {
    private Map<String, DefaultItemDto> defaultItemDtoMap;
    private Map<String, VasItemDto> vasItemDtoMap;
    private Map<String, DigitalItemDto> digitalItemDtoMap;

}
