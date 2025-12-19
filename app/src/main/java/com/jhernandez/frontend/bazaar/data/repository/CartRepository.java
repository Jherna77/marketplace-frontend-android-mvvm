package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.ItemMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.port.CartRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing cart-related operations.
 */
@RequiredArgsConstructor
public class CartRepository implements CartRepositoryPort {

    private final ApiService apiService;

    @Override
    public void loadUserCart(Long userId, TypeCallback<List<Item>> callback) {
        apiService.loadUserCart(userId)
                .enqueue(CallbackDelegator.delegate(
                        "loadUserCart",
                        response ->
                                callback.onSuccess(ItemMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void addItemToUserCart(Long userId, Item item, SuccessCallback callback) {
        apiService.addItemToUserCart(userId, ItemMapper.toDto(item))
                .enqueue(CallbackDelegator.delegate("addItemToCart", callback));
    }

    @Override
    public void removeItemFromUserCart(Long userId, Long itemId, TypeCallback<List<Item>> callback) {
        apiService.removeItemFromUserCart(userId, itemId)
                .enqueue(CallbackDelegator.delegate(
                        "removeItemFromCart",
                        response ->
                                callback.onSuccess(ItemMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void updateItemQuantity(Long userId, Long itemId, Integer quantity, TypeCallback<List<Item>> callback) {
        apiService.updateItemQuantity(userId, itemId, quantity )
                .enqueue(CallbackDelegator.delegate(
                        "updateItemQuantity",
                        response ->
                                callback.onSuccess(ItemMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void clearUserCart(Long userId, TypeCallback<List<Item>> callback) {
        apiService.clearUserCart(userId)
                .enqueue(CallbackDelegator.delegate("clearUserCart", response ->
                                callback.onSuccess(ItemMapper.toDomainList(response)),
                                callback::onError));
    }
}
