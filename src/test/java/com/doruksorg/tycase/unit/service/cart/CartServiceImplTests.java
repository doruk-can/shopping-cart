package com.doruksorg.tycase.unit.service.cart;

import com.doruksorg.tycase.entity.Cart;
import com.doruksorg.tycase.exception.CartNotFoundException;
import com.doruksorg.tycase.mapper.CartMapper;
import com.doruksorg.tycase.model.dto.cart.CartDisplayDto;
import com.doruksorg.tycase.model.dto.cart.CartDto;
import com.doruksorg.tycase.model.dto.cart.ItemMapsContainerDto;
import com.doruksorg.tycase.model.dto.item.DefaultItemDto;
import com.doruksorg.tycase.model.dto.item.DigitalItemDto;
import com.doruksorg.tycase.model.dto.item.VasItemDto;
import com.doruksorg.tycase.model.dto.promotion.PromotionDetailsDto;
import com.doruksorg.tycase.model.enums.CartType;
import com.doruksorg.tycase.model.enums.ItemType;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.model.mockapi.request.RemoveItemRequest;
import com.doruksorg.tycase.repository.CartRepository;
import com.doruksorg.tycase.service.cart.service.CartServiceImpl;
import com.doruksorg.tycase.service.item.service.ItemValidationService;
import com.doruksorg.tycase.service.promotion.service.PromotionService;
import com.doruksorg.tycase.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTests {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PromotionService promotionService;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ItemValidationService itemValidationService;

    @Mock
    private Cart cart;

    private CartServiceImpl cartServiceImpl;

    private static final String userId = "testUser123";


    @BeforeEach
    public void setup() {
        cartServiceImpl = new CartServiceImpl(cartRepository, promotionService, cartMapper, itemValidationService);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
    }

    @Test
    public void shouldAddDefaultItemToCart() {
        CartDto cartDto = createEmptyCartDto();
        AddItemRequest addItemRequest = createAddItemRequest(1, 1, 1, 100.0, 1);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addItem(userId, addItemRequest);

        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartMapper, times(1)).cartEntityToCartDto(cart);
        verify(cartMapper, times(1)).cartDtoToCartEntity(cartDto);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void shouldThrowCartNotFoundExceptionWhenAddingItemAndNoCartExistsForUser() {
        AddItemRequest addItemRequest = createAddItemRequest(1, 1, 1, 100.0, 1);

        when(cartRepository.findByUserId(userId)).thenThrow(new CartNotFoundException("Cart not found"));

        assertThrows(CartNotFoundException.class, () -> cartServiceImpl.addItem(userId, addItemRequest));
        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void shouldIncreaseQuantityAndCalculatePricesWhenAddingSameTypeDefaultItem() {
        final int defaultItemId = 23423;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(Map.of(String.valueOf(defaultItemId),
                createDefaultItemDto(defaultItemId, 1, 1, 50.0, 2)));
        AddItemRequest addItemRequest = createAddItemRequest(defaultItemId, 1, 1, 50.0, 4);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addItem(userId, addItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertEquals(6, cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(addItemRequest.getItemId())).getQuantity());
        assertEquals(300.0, cartDto.getTotalPrice());
        assertEquals(300.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
        assertEquals(CartType.DEFAULT, cartDto.getCartType());
    }

    @Test
    public void shouldAddDigitalItemToCart() {
        CartDto cartDto = createEmptyCartDto();
        AddItemRequest addItemRequest = createAddItemRequest(1, Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID, 1, 100.0, 1);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addItem(userId, addItemRequest);

        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartMapper, times(1)).cartEntityToCartDto(cart);
        verify(cartMapper, times(1)).cartDtoToCartEntity(cartDto);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void shouldIncreaseQuantityAndRecalculatePriceWhenAddingSameTypeDigitalItem() {
        final int digitalItemId = Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DIGITAL);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDigitalItemDtoMap(Map.of(String.valueOf(digitalItemId),
                createDigitalItemDto(digitalItemId, digitalItemId, 1, 50.0, 2)));
        AddItemRequest addItemRequest = createAddItemRequest(digitalItemId, Constants.CategoryIds.DIGITAL_ITEM_CATEGORY_ID, 1, 50.0, 3); //quantity 3

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addItem(userId, addItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertEquals(5, cartDto.getItemMapsContainer().getDigitalItemDtoMap().get(String.valueOf(addItemRequest.getItemId())).getQuantity());
        assertEquals(250.0, cartDto.getTotalPrice());
        assertEquals(250.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
        assertEquals(CartType.DIGITAL, cartDto.getCartType());
    }

    @Test
    public void shouldAddVasItemToCart() {
        CartDto cartDto = createEmptyCartDto();
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(Map.of("111", createDefaultItemDto(111, 1, Constants.CategoryIds.FURNITURE_ID, 100.0, 1)));
        AddVasItemRequest addVasItemRequest = createAddVasItemRequest(111, 222, Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, Constants.SellerIds.VAS_ITEM_SELLER_ID, 100.0, 1);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addVasItem(userId, addVasItemRequest);

        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartMapper, times(1)).cartEntityToCartDto(cart);
        verify(cartMapper, times(1)).cartDtoToCartEntity(cartDto);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void shouldIncreaseQuantityRecalculatePriceAndUpdateDefaultItemWhenAddingVasItem() {
        final int defaultItemId = 111;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        VasItemDto vasItemDto = createVasItemDto(1, 222, Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, Constants.SellerIds.VAS_ITEM_SELLER_ID, 50.0, 1);
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of(String.valueOf(defaultItemId), createDefaultItemDto(111, 1, Constants.CategoryIds.FURNITURE_ID, 100.0, 1))));
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of(String.valueOf(vasItemDto.getItemId()), vasItemDto)));
        AddVasItemRequest addVasItemRequest = createAddVasItemRequest(defaultItemId, 222, Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, Constants.SellerIds.VAS_ITEM_SELLER_ID, 50.0, 1);


        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.addVasItem(userId, addVasItemRequest);

        assertTrue(cartDto.getItemMapsContainer().getDefaultItemDtoMap()
                .get(String.valueOf(addVasItemRequest.getItemId())).getSubItemIdSet().contains(String.valueOf(addVasItemRequest.getVasItemId())));
        assertEquals(2, cartDto.getItemMapsContainer().getVasItemDtoMap().get(String.valueOf(addVasItemRequest.getVasItemId())).getQuantity());
        assertEquals(200.0, cartDto.getTotalPrice());
        assertEquals(200.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
    }

    @Test
    public void shouldRemoveDefaultItemAndAssociatedVasItemsFromCart() {
        final int defaultItemId = 123;
        final int vasItemId = 567;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of(String.valueOf(defaultItemId),
                createDefaultItemDto(defaultItemId, Constants.CategoryIds.FURNITURE_ID, 1, 50.0, 4),
                "234", createDefaultItemDto(234, Constants.CategoryIds.FURNITURE_ID, 1, 40.0, 2))));
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of(String.valueOf(vasItemId), createVasItemDto(defaultItemId, vasItemId, 1, 1, 40.0, 2))));
        cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(defaultItemId)).setSubItemIdSet(new HashSet<>(Set.of(String.valueOf(vasItemId))));
        RemoveItemRequest removeItemRequest = createRemoveItemRequest(defaultItemId);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.removeItem(userId, removeItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertNull(cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(removeItemRequest.getItemId())));
        assertNull(cartDto.getItemMapsContainer().getVasItemDtoMap());
        assertEquals(80.0, cartDto.getTotalPrice());
        assertEquals(80.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
    }

    @Test
    public void shouldRemoveLastDefaultItemFromCart() {
        final int defaultItemId = 123;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of(String.valueOf(defaultItemId),
                createDefaultItemDto(defaultItemId, 1, 1, 50.0, 4))));
        RemoveItemRequest removeItemRequest = createRemoveItemRequest(defaultItemId);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.removeItem(userId, removeItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertNull(cartDto.getItemMapsContainer().getDefaultItemDtoMap());
        assertEquals(0.0, cartDto.getTotalPrice());
        assertEquals(0.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
        assertNull(cartDto.getCartType());
    }

    @Test
    public void shouldRemoveVasItemAndUpdateAssociatedDefaultItem() {
        final int defaultItemId = 123;
        final int vasItemId = 567;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of(String.valueOf(defaultItemId),
                createDefaultItemDto(defaultItemId, Constants.CategoryIds.FURNITURE_ID, 1, 50.0, 4))));
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of(String.valueOf(vasItemId), createVasItemDto(defaultItemId, vasItemId, Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, Constants.SellerIds.VAS_ITEM_SELLER_ID, 40.0, 2))));
        cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(defaultItemId)).setSubItemIdSet(new HashSet<>(Set.of(String.valueOf(vasItemId))));
        RemoveItemRequest removeItemRequest = createRemoveItemRequest(vasItemId);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.removeItem(userId, removeItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertNull(cartDto.getItemMapsContainer().getVasItemDtoMap());
        assertNull(cartDto.getItemMapsContainer().getDefaultItemDtoMap().get(String.valueOf(defaultItemId)).getSubItemIdSet());
        assertEquals(200.0, cartDto.getTotalPrice());
        assertEquals(200.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
    }

    @Test
    public void shouldRemoveLastDigitalItemFromCart() {
        final int digitalItemId = 123;
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DIGITAL);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDigitalItemDtoMap(new HashMap<>(Map.of(String.valueOf(digitalItemId),
                createDigitalItemDto(digitalItemId, 1, 1, 50.0, 4))));
        RemoveItemRequest removeItemRequest = createRemoveItemRequest(digitalItemId);

        setupPromotionService();
        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(cartDto)).thenReturn(cart);

        cartServiceImpl.removeItem(userId, removeItemRequest);

        verify(cartRepository, times(1)).save(cart);

        assertNull(cartDto.getItemMapsContainer().getDigitalItemDtoMap());
        assertEquals(0.0, cartDto.getTotalPrice());
        assertEquals(0.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
        assertNull(cartDto.getCartType());
    }


    @Test
    public void shouldResetCartToInitialState() {
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("123", createDefaultItemDto(123, 1, 1, 50.0, 4))));
        cartDto.getItemMapsContainer().setVasItemDtoMap(new HashMap<>(Map.of("435", createVasItemDto(123, 435, Constants.CategoryIds.VAS_ITEM_CATEGORY_ID, Constants.SellerIds.VAS_ITEM_SELLER_ID, 40.0, 2))));

        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartEntity(any(CartDto.class))).thenReturn(cart);

        cartServiceImpl.resetCart(userId);

        verify(cartRepository, times(1)).save(cart);
        assertNull(cartDto.getCartType());
        assertNull(cartDto.getItemMapsContainer());
        assertEquals(0.0, cartDto.getTotalPrice());
        assertEquals(0.0, cartDto.getFinalPrice());
        assertEquals(0.0, cartDto.getDiscountApplied());
        assertEquals(-1, cartDto.getAppliedPromotionId());
    }

    @Test
    public void shouldDisplayCartContents() {
        CartDto cartDto = createEmptyCartDto();
        cartDto.setCartType(CartType.DEFAULT);
        cartDto.setItemMapsContainer(new ItemMapsContainerDto());
        cartDto.getItemMapsContainer().setDefaultItemDtoMap(new HashMap<>(Map.of("123", createDefaultItemDto(123, 1, 1, 50.0, 4))));

        when(cartMapper.cartEntityToCartDto(cart)).thenReturn(cartDto);
        when(cartMapper.cartDtoToCartDisplayDto(cartDto)).thenReturn(new CartDisplayDto());

        String response = cartServiceImpl.displayCart(userId);

        assertNotNull(response);
    }


    private CartDto createEmptyCartDto() {
        return CartDto.builder()
                .userId(userId)
                .totalPrice(0.0)
                .discountApplied(0.0)
                .finalPrice(0.0)
                .appliedPromotionId(-1)
                .cartType(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(1)
                .build();
    }

    private AddItemRequest createAddItemRequest(int itemId, int categoryId, int sellerId, double price, int quantity) {
        return AddItemRequest.builder()
                .itemId(itemId)
                .categoryId(categoryId)
                .sellerId(sellerId)
                .price(price)
                .quantity(quantity)
                .build();
    }

    private RemoveItemRequest createRemoveItemRequest(int itemId) {
        return RemoveItemRequest.builder()
                .itemId(itemId)
                .build();
    }

    private AddVasItemRequest createAddVasItemRequest(int itemId, int vasItemId, int vasCategoryId, int vasSellerId, double price, int quantity) {
        return AddVasItemRequest.builder()
                .itemId(itemId)
                .vasItemId(vasItemId)
                .vasCategoryId(vasCategoryId)
                .vasSellerId(vasSellerId)
                .price(price)
                .quantity(quantity)
                .build();
    }

    private DefaultItemDto createDefaultItemDto(int itemId, int categoryId, int sellerId, double price, int quantity) {
        return DefaultItemDto.builder()
                .itemId(itemId)
                .categoryId(categoryId)
                .sellerId(sellerId)
                .price(price)
                .quantity(quantity)
                .itemType(ItemType.DEFAULT)
                .subItemIdSet(null)
                .build();
    }

    private DigitalItemDto createDigitalItemDto(int itemId, int categoryId, int sellerId, double price, int quantity) {
        return DigitalItemDto.builder()
                .itemId(itemId)
                .categoryId(categoryId)
                .sellerId(sellerId)
                .price(price)
                .quantity(quantity)
                .itemType(ItemType.DIGITAL)
                .build();
    }

    private VasItemDto createVasItemDto(int parentId, int itemId, int categoryId, int sellerId, double price, int quantity) {
        return VasItemDto.builder()
                .parentId(parentId)
                .itemId(itemId)
                .categoryId(categoryId)
                .sellerId(sellerId)
                .price(price)
                .quantity(quantity)
                .itemType(ItemType.VAS)
                .build();
    }

    private void setupPromotionService() {
        PromotionDetailsDto promotionDetailsDto = PromotionDetailsDto.builder()
                .appliedDiscount(0.0)
                .appliedPromotionId(-1)
                .build();
        when(promotionService.applyBestPromotion(any(CartDto.class))).thenReturn(promotionDetailsDto);
    }
}


