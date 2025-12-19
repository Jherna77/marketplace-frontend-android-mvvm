package com.jhernandez.frontend.bazaar.ui.product.admin;

/*
 * ViewState for managing the state of the admin products dashboard.
 * It currently tracks whether the filter is enabled.
 */
public record AdminProductsViewState(Boolean filterEnabled) {

    public AdminProductsViewState withFilter(Boolean filter) {
        return new AdminProductsViewState(filter);
    }

}
