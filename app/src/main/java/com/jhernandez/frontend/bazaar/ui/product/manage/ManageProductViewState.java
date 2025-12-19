package com.jhernandez.frontend.bazaar.ui.product.manage;

/*
 * ViewState for managing the state of the product management screen.
 * It tracks whether the product is being updated, loading state, enabled state, and discount state.
 */
public record ManageProductViewState(Boolean isUpdate, Boolean isLoading, Boolean isEnabled, Boolean hasDiscount) {

    public ManageProductViewState withLoading(Boolean loading) {
        return new ManageProductViewState(isUpdate, loading, isEnabled, hasDiscount);
    }

    public ManageProductViewState withEnabled(Boolean enabled) {
        return new ManageProductViewState(isUpdate, isLoading, enabled, hasDiscount);
    }

    public ManageProductViewState withDiscount(Boolean discount) {
        return new ManageProductViewState(isUpdate, isLoading, isEnabled, discount);
    }

}
