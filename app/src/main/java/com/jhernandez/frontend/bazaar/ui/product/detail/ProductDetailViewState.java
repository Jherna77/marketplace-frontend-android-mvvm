package com.jhernandez.frontend.bazaar.ui.product.detail;

/*
 * ViewState for managing the state of the product detail screen.
 * It tracks whether the product is a favorite, has free shipping, and has a discount.
 */
public record ProductDetailViewState(Boolean isFavourite, Boolean isFreeShipping, Boolean hasDiscount) {

    public ProductDetailViewState withFavourite(Boolean favourite) {
        return new ProductDetailViewState(favourite, isFreeShipping, hasDiscount);
    }

    public ProductDetailViewState withFreeShipping(Boolean freeShipping) {
        return new ProductDetailViewState(isFavourite, freeShipping, hasDiscount);
    }

    public ProductDetailViewState withDiscount(Boolean discount) {
        return new ProductDetailViewState(isFavourite, isFreeShipping, discount);
    }

}
