package com.doruksorg.tycase.mapper;

import com.doruksorg.tycase.entity.Cart;
import com.doruksorg.tycase.model.dto.cart.CartDisplayDto;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import org.mapstruct.*;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface CartMapper {

    Cart cartDtoToCartEntity(CartDto cartDto);

    CartDto cartEntityToCartDto(Cart cart);


    @BeanMapping(qualifiedByName = "cartDtoToCartDisplayDto")
    CartDisplayDto cartDtoToCartDisplayDto(CartDto cartDto);

    @Named("cartDtoToCartDisplayDto")
    @AfterMapping
    default void afterCartDtoToCartDisplayDto(@MappingTarget CartDisplayDto cartDisplayDto, CartDto cartDto) {
        if (cartDto.getItemMapsContainer() != null) {
            cartDisplayDto.setItemDtoList(new ArrayList<>());
            if (cartDto.getItemMapsContainer().getDefaultItemDtoMap() != null) {
                cartDisplayDto.getItemDtoList().addAll(cartDto.getItemMapsContainer().getDefaultItemDtoMap().values());
            }
            if (cartDto.getItemMapsContainer().getVasItemDtoMap() != null) {
                cartDisplayDto.getItemDtoList().addAll(cartDto.getItemMapsContainer().getVasItemDtoMap().values());
            }
            if (cartDto.getItemMapsContainer().getDigitalItemDtoMap() != null) {
                cartDisplayDto.getItemDtoList().addAll(cartDto.getItemMapsContainer().getDigitalItemDtoMap().values());
            }
        }
    }


}
