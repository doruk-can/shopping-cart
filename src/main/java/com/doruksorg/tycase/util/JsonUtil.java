package com.doruksorg.tycase.util;

import com.doruksorg.tycase.model.dto.cart.CartDisplayDto;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.enums.CartType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String jsonResponse(boolean success, String message) {
        return String.format("{\"result\": %b, \"message\": \"%s\"}", success, message);
    }

    public static String jsonDisplayResponse(boolean success, CartDisplayDto cartDisplayDto, CartDto cartDto) {
        try {
            String message = objectMapper.writeValueAsString(cartDisplayDto);
            String mainResponse = String.format("{\"result\": %b, \"message\": %s}", success, message);

            if (cartDto.getItemMapsContainer() == null) {
                return mainResponse;
            }

            String detailedItems = getDetailedItems(cartDto);
            return mainResponse + detailedItems;
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            return jsonResponse(false, "Error processing JSON");
        }
    }

    private static String getDetailedItems(CartDto cartDto) throws JsonProcessingException {
        StringBuilder detailedItems = new StringBuilder();

        if (CartType.DEFAULT.equals(cartDto.getCartType()) && cartDto.getItemMapsContainer().getDefaultItemDtoMap() != null) {
            for (DefaultItemDto defaultItemDto : cartDto.getItemMapsContainer().getDefaultItemDtoMap().values()) {
                detailedItems.append(String.format("\nty.item -> %s", objectMapper.writeValueAsString(defaultItemDto)));
                if (defaultItemDto.getSubItemIdSet() != null) {
                    for (String vasItemId : defaultItemDto.getSubItemIdSet()) {
                        VasItemDto vasItem = cartDto.getItemMapsContainer().getVasItemDtoMap().get(vasItemId);
                        detailedItems.append(String.format("\nty.vasItem -> %s", objectMapper.writeValueAsString(vasItem)));
                    }
                }
            }
        } else if (CartType.DIGITAL.equals(cartDto.getCartType()) && cartDto.getItemMapsContainer().getDigitalItemDtoMap() != null) {
            for (DigitalItemDto digitalItemDto : cartDto.getItemMapsContainer().getDigitalItemDtoMap().values()) {
                detailedItems.append(String.format("\nty.digitalItem -> %s", objectMapper.writeValueAsString(digitalItemDto)));
            }
        }

        return detailedItems.toString();
    }
}
