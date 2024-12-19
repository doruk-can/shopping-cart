package com.doruksorg.tycase.service.cart.service;

import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.model.mockapi.request.RemoveItemRequest;

public interface CartService {

    String addItem(String userId, AddItemRequest addItemRequest);

    String addVasItem(String userId, AddVasItemRequest addVasItemRequest);

    String removeItem(String userId, RemoveItemRequest removeItemRequest);

    String resetCart(String userId);

    String displayCart(String userId);

}
