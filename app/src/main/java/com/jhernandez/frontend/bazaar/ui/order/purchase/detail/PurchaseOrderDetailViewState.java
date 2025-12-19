package com.jhernandez.frontend.bazaar.ui.order.purchase.detail;

/*
 * View state for PurchaseOrderDetailActivity.
 * Contains loading state.
 */
public record PurchaseOrderDetailViewState(Boolean isLoading) {

    public PurchaseOrderDetailViewState withLoading(Boolean loading) {
        return new PurchaseOrderDetailViewState(loading);
    }
}
