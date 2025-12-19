package com.jhernandez.frontend.bazaar.ui.product.shop;

/*
 * ViewState for managing the state of the shop products screen.
 * It tracks loading state and whether there are products to display.
 */
public record ShopProductsViewState(Boolean isLoading, Boolean hasProducts) {

    public ShopProductsViewState withLoading(Boolean loading) {
        return new ShopProductsViewState(loading, hasProducts);
    }

    public ShopProductsViewState withProducts(Boolean products) {
        return new ShopProductsViewState(isLoading, products);
    }
}
