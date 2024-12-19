package com.doruksorg.tycase.service.cart.service;

import com.doruksorg.tycase.entity.Cart;
import com.doruksorg.tycase.exception.CartNotFoundException;
import com.doruksorg.tycase.exception.InvalidItemCategoryException;
import com.doruksorg.tycase.mapper.CartMapper;
import com.doruksorg.tycase.model.dto.cart.CartDisplayDto;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.ItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.dto.promotion.PromotionDetailsDto;
import com.doruksorg.tycase.model.enums.CartType;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.model.mockapi.request.RemoveItemRequest;
import com.doruksorg.tycase.repository.CartRepository;
import com.doruksorg.tycase.service.item.factory.ItemFactory;
import com.doruksorg.tycase.service.item.service.ItemValidationService;
import com.doruksorg.tycase.service.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.doruksorg.tycase.util.JsonUtil.jsonDisplayResponse;
import static com.doruksorg.tycase.util.JsonUtil.jsonResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final PromotionService promotionService;
    private final CartMapper cartMapper;
    private final ItemValidationService itemValidationService;

    @Override
    public String addItem(String userId, AddItemRequest addItemRequest) {
        log.info("Adding item for user: {}", userId);
        CartDto cartDto = getCartByUserId(userId);
        ItemDto itemDto = ItemFactory.createItem(addItemRequest, cartDto);
        log.debug("Created itemDto: {}", itemDto);
        itemValidationService.validateItemCompatibility(cartDto, itemDto);
        addItemToCart(cartDto, itemDto, String.valueOf(itemDto.getItemId()));
        itemValidationService.validateItemAddition(cartDto);
        recalculateTotals(cartDto);
        itemValidationService.validateTotalCartPrice(cartDto.getTotalPrice());
        Cart cart = cartMapper.cartDtoToCartEntity(cartDto);
        cartRepository.save(cart);
        log.info("Item added successfully for user: {}", userId);
        return jsonResponse(true, "Item added successfully.");
    }

    @Override
    public String addVasItem(String userId, AddVasItemRequest addVasItemRequest) {
        log.info("Adding VasItem for user: {}", userId);
        CartDto cartDto = getCartByUserId(userId);
        VasItemDto vasItemDto = (VasItemDto) ItemFactory.createItem(addVasItemRequest);
        log.debug("Initialized VasItemDto: {}", vasItemDto);

        initializeItemMaps(cartDto);

        itemValidationService.validateVasItemCompatibility(cartDto, vasItemDto);
        itemValidationService.validateVasItemQuantity(cartDto, vasItemDto);

        addVasItemToCart(cartDto, vasItemDto);

        recalculateTotals(cartDto);
        itemValidationService.validateTotalCartPrice(cartDto.getTotalPrice());

        Cart cart = cartMapper.cartDtoToCartEntity(cartDto);
        cartRepository.save(cart);

        log.info("VasItem added successfully for user: {}", userId);
        return jsonResponse(true, "VasItem added successfully.");
    }

    @Override
    public String removeItem(String userId, RemoveItemRequest removeItemRequest) {
        log.info("Removing item for user: {}", userId);
        CartDto cartDto = getCartByUserId(userId);

        if (cartDto.getItemMapsContainer() == null) {
            log.warn("No items to delete for user: {}", userId);
            return jsonResponse(false, "There is no item to delete.");
        }

        List<ItemDto> removedItemDtoList = removeItemsFromCart(cartDto, removeItemRequest);

        if (removedItemDtoList.isEmpty()) {
            log.warn("Item not found in cart for user: {}", userId);
            return jsonResponse(false, "Item not found in cart.");
        }

        recalculateTotals(cartDto);
        itemValidationService.validateTotalCartPrice(cartDto.getTotalPrice());

        Cart cart = cartMapper.cartDtoToCartEntity(cartDto);
        cartRepository.save(cart);

        String removedItemIds = removedItemDtoList.stream()
                .map(itemDto -> Integer.toString(itemDto.getItemId()))
                .collect(Collectors.joining(", "));

        log.info("Items removed successfully for user: {}. IDs: {}", userId, removedItemIds);
        return jsonResponse(true, "Items removed successfully. IDs: " + removedItemIds);
    }

    @Override
    public String resetCart(String userId) {
        log.info("Resetting cart for user: {}", userId);
        CartDto cartDto = getCartByUserId(userId);
        cartDto.reset();
        Cart cart = cartMapper.cartDtoToCartEntity(cartDto);
        cartRepository.save(cart);
        log.info("Cart reset successfully for user: {}", userId);
        return jsonResponse(true, "Items removed successfully");
    }

    @Override
    public String displayCart(String userId) {
        log.info("Displaying cart for user: {}", userId);
        CartDto cartDto = getCartByUserId(userId);
        CartDisplayDto cartDisplayDto = cartMapper.cartDtoToCartDisplayDto(cartDto);
        log.info("Cart displayed successfully for user: {}", userId);
        return jsonDisplayResponse(true, cartDisplayDto, cartDto);
    }

    private void addItemToCart(CartDto cartDto, ItemDto itemDto, String itemId) {
        if (cartDto.getItemMapsContainer() == null) {
            cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        }
        switch (itemDto.getItemType()) {
            case DEFAULT:
                addDefaultItemToCart(cartDto, itemDto, itemId);
                break;
            case DIGITAL:
                addDigitalItemToCart(cartDto, itemDto, itemId);
                break;
            default:
                throw new InvalidItemCategoryException("Invalid item category.");
        }
    }

    private void addDefaultItemToCart(CartDto cartDto, ItemDto itemDto, String itemId) {
        Map<String, DefaultItemDto> defaultItemDtoMap = getDefaultItemDtoMap(cartDto);
        DefaultItemDto existingDefaultItemDto = defaultItemDtoMap.get(itemId);
        if (existingDefaultItemDto != null) {
            log.debug("Existing item found in cart. Updating quantity.");
            existingDefaultItemDto.setQuantity(existingDefaultItemDto.getQuantity() + itemDto.getQuantity());
        } else {
            log.debug("No existing item found in cart. Adding new item.");
            defaultItemDtoMap.put(itemId, (DefaultItemDto) itemDto);
        }
    }

    private void addDigitalItemToCart(CartDto cartDto, ItemDto itemDto, String itemId) {
        Map<String, DigitalItemDto> digitalItemDtoMap = getDigitalItemDtoMap(cartDto);
        DigitalItemDto existingDigitalItemDto = digitalItemDtoMap.get(itemId);
        if (existingDigitalItemDto != null) {
            log.debug("Existing digital item found in cart. Updating quantity.");
            existingDigitalItemDto.setQuantity(existingDigitalItemDto.getQuantity() + itemDto.getQuantity());
        } else {
            log.debug("No existing digital item found in cart. Adding new item.");
            digitalItemDtoMap.put(itemId, (DigitalItemDto) itemDto);
        }
    }

    private Map<String, DefaultItemDto> getDefaultItemDtoMap(CartDto cartDto) {
        cartDto.setCartType(CartType.DEFAULT);
        if (cartDto.getItemMapsContainer().getDefaultItemDtoMap() == null) {
            cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>());
        }
        return cartDto.getItemMapsContainer().getDefaultItemDtoMap();
    }

    private Map<String, DigitalItemDto> getDigitalItemDtoMap(CartDto cartDto) {
        cartDto.setCartType(CartType.DIGITAL);
        if (cartDto.getItemMapsContainer().getDigitalItemDtoMap() == null) {
            cartDto.getItemMapsContainer().setDigitalItemDtoMap(new HashMap<>());
        }
        return cartDto.getItemMapsContainer().getDigitalItemDtoMap();
    }

    private void initializeItemMaps(CartDto cartDto) {
        if (cartDto.getItemMapsContainer() == null) {
            cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        }
        if (cartDto.getItemMapsContainer().getVasItemDtoMap() == null) {
            cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>());
        }
    }

    private void addVasItemToCart(CartDto cartDto, VasItemDto vasItemDto) {
        Map<String, VasItemDto> vasItemMap = cartDto.getItemMapsContainer().getVasItemDtoMap();
        VasItemDto currentVasItem = vasItemMap.get(String.valueOf(vasItemDto.getItemId()));
        if (currentVasItem != null) {
            log.debug("Existing VasItem found in cart. Updating quantity.");
            vasItemDto.setQuantity(currentVasItem.getQuantity() + vasItemDto.getQuantity());
        }
        vasItemMap.put(String.valueOf(vasItemDto.getItemId()), vasItemDto);

        DefaultItemDto defaultItemDto = cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(vasItemDto.getParentId()));
        if (defaultItemDto.getSubItemIdSet() == null) {
            log.debug("No existing subItemIdSet found. Creating new set.");
            defaultItemDto.setSubItemIdSet(new HashSet<>());
        }
        defaultItemDto.getSubItemIdSet().add(String.valueOf(vasItemDto.getItemId()));
    }

    private List<ItemDto> removeItemsFromCart(CartDto cartDto, RemoveItemRequest removeItemRequest) {
        List<ItemDto> removedItemDtoList = new ArrayList<>();

        if (cartDto.getItemMapsContainer().getDefaultItemDtoMap() != null) {
            removeDefaultItemAndItsVasItems(cartDto, removeItemRequest, removedItemDtoList);
        }

        if (cartDto.getItemMapsContainer().getDigitalItemDtoMap() != null) {
            removeDigitalItem(cartDto, removeItemRequest, removedItemDtoList);
        }

        if (cartDto.getItemMapsContainer().getVasItemDtoMap() != null) {
            removeVasItem(cartDto, removeItemRequest, removedItemDtoList);
        }

        return removedItemDtoList;
    }

    private void removeDefaultItemAndItsVasItems(CartDto cartDto, RemoveItemRequest removeItemRequest, List<ItemDto> removedItemDtoList) {
        DefaultItemDto removedDefaultItemDto = cartDto.getItemMapsContainer().getDefaultItemDtoMap().remove(String.valueOf(removeItemRequest.getItemId()));
        if (removedDefaultItemDto != null) {
            log.debug("Default item found in cart. Removing item.");
            removedItemDtoList.add(removedDefaultItemDto);
            if (removedDefaultItemDto.getSubItemIdSet() != null) {
                for (String key : removedDefaultItemDto.getSubItemIdSet()) {
                    VasItemDto removedVasItem = cartDto.getItemMapsContainer().getVasItemDtoMap().remove(key);
                    if (removedVasItem != null) {
                        log.debug("VasItem found in cart. Removing item.");
                        removedItemDtoList.add(removedVasItem);
                    }
                }
                removedDefaultItemDto.getSubItemIdSet().clear();
            }
        } else {
            log.warn("No default item found in cart for user: {}", cartDto.getUserId());
        }

        if (cartDto.getItemMapsContainer().getDefaultItemDtoMap().isEmpty()) {
            cartDto.setCartType(null);
            cartDto.getItemMapsContainer().setDefaultItemDtoMap(null);
        }
    }

    private void removeDigitalItem(CartDto cartDto, RemoveItemRequest removeItemRequest, List<ItemDto> removedItemDtoList) {
        ItemDto removedItem = cartDto.getItemMapsContainer().getDigitalItemDtoMap().remove(String.valueOf(removeItemRequest.getItemId()));
        if (removedItem != null) {
            log.debug("Digital item found in cart. Removing item.");
            removedItemDtoList.add(removedItem);
        }
        if (cartDto.getItemMapsContainer().getDigitalItemDtoMap().isEmpty()) {
            cartDto.setCartType(null);
            cartDto.getItemMapsContainer().setDigitalItemDtoMap(null);
        }
    }

    private void removeVasItem(CartDto cartDto, RemoveItemRequest removeItemRequest, List<ItemDto> removedItemDtoList) {
        VasItemDto removedItem = cartDto.getItemMapsContainer().getVasItemDtoMap().remove(String.valueOf(removeItemRequest.getItemId()));
        if (removedItem != null) {
            log.debug("VasItem found in cart. Removing item.");
            DefaultItemDto defaultItemDto = cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(removedItem.getParentId()));
            boolean removedFromDefaultItem = defaultItemDto.getSubItemIdSet().remove(String.valueOf(removedItem.getItemId()));
            if (removedFromDefaultItem) {
                log.warn("Vas item with ID {} has been removed from the SubItemIdSet of the Default item with ID {}. User ID: {}", removedItem.getItemId(), defaultItemDto.getItemId(), cartDto.getUserId());
            }
            if (defaultItemDto.getSubItemIdSet().isEmpty()) {
                defaultItemDto.setSubItemIdSet(null);
            }
            removedItemDtoList.add(removedItem);
        }
        if (cartDto.getItemMapsContainer().getVasItemDtoMap().isEmpty()) {
            cartDto.getItemMapsContainer().setVasItemDtoMap(null);
        }
    }

    private void recalculateTotals(CartDto cartDto) {
        ItemMapsContainerDto itemMapsContainer = cartDto.getItemMapsContainer();

        double defaultItemsTotalPrice = calculateTotalPrice(itemMapsContainer.getDefaultItemDtoMap());
        log.info("Default items total price calculated: {}", defaultItemsTotalPrice);
        double vasItemsTotalPrice = calculateTotalPrice(itemMapsContainer.getVasItemDtoMap());
        log.info("Vas items total price calculated: {}", vasItemsTotalPrice);
        double digitalItemsTotalPrice = calculateTotalPrice(itemMapsContainer.getDigitalItemDtoMap());
        log.info("Digital items total price calculated: {}", digitalItemsTotalPrice);

        double totalPrice = defaultItemsTotalPrice + vasItemsTotalPrice + digitalItemsTotalPrice;
        cartDto.setTotalPrice(totalPrice);
        log.info("Total price calculated: {}", totalPrice);

        PromotionDetailsDto promotionDetailsDto = promotionService.applyBestPromotion(cartDto);
        cartDto.setDiscountApplied(promotionDetailsDto.getAppliedDiscount());
        cartDto.setAppliedPromotionId(promotionDetailsDto.getAppliedPromotionId());
        log.info("Applied promotion ID: {}", promotionDetailsDto.getAppliedPromotionId());

        applyDiscount(cartDto, cartDto.getDiscountApplied());
    }

    private double calculateTotalPrice(Map<String, ? extends ItemDto> itemDtoMap) {
        if (itemDtoMap == null) {
            return 0;
        }
        return itemDtoMap.values().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
    }


    private void applyDiscount(CartDto cartDto, double discount) {
        double finalPrice = cartDto.getTotalPrice() - discount;
        cartDto.setFinalPrice(finalPrice);
        log.info("Discount applied: {}, New final price: {}", discount, finalPrice);
    }

    private CartDto getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::cartEntityToCartDto)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
    }


}