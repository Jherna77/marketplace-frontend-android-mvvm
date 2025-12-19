package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Item;

import java.util.List;

/*
 * Interface representing the CartRepositoryPort.
 */
public interface CartRepositoryPort {

    void loadUserCart(Long userId, TypeCallback<List<Item>> callback);
    void addItemToUserCart(Long userId, Item item, SuccessCallback callback);
    void removeItemFromUserCart(Long userId, Long itemId, TypeCallback<List<Item>> callback);
    void updateItemQuantity(Long userId, Long itemId, Integer quantity, TypeCallback<List<Item>> callback);
    void clearUserCart(Long userId, TypeCallback<List<Item>> callback);
}
