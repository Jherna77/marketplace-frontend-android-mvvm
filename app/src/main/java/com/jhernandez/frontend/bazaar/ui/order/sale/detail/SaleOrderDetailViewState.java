package com.jhernandez.frontend.bazaar.ui.order.sale.detail;

/*
 * View state for SaleOrderDetailActivity.
 * Contains loading state and error state.
 */
public record SaleOrderDetailViewState(Boolean isLoading, Boolean hasErrors) {

    public SaleOrderDetailViewState withLoading(Boolean loading) {
        return new SaleOrderDetailViewState(loading, hasErrors);
    }

    public SaleOrderDetailViewState withError(Boolean error) {
        return new SaleOrderDetailViewState(isLoading, error);
    }

}
