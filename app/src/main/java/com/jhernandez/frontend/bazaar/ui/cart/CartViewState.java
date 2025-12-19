package com.jhernandez.frontend.bazaar.ui.cart;

/*
 * View state for CartFragment.
 * Holds the UI state related to the shopping cart.
 */
public record CartViewState(Boolean isLoading, Boolean isEmpty) {

    public CartViewState withLoading(Boolean loading) {
        return new CartViewState(loading, isEmpty);
    }

    public CartViewState withEmpty(Boolean empty) {
        return new CartViewState(isLoading, empty);
    }
}
