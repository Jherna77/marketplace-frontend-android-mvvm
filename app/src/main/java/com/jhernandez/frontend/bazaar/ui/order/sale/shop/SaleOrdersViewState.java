package com.jhernandez.frontend.bazaar.ui.order.sale.shop;

/*
 * View state for SaleOrdersActivity.
 * Contains filter state and whether there are results.
 */
public record SaleOrdersViewState(Boolean filterEnabled, Boolean hasResults) {

    public SaleOrdersViewState withFilter(Boolean filter) {
        return new SaleOrdersViewState(filter, hasResults);
    }

    public SaleOrdersViewState withResults(Boolean results) {
        return new SaleOrdersViewState(filterEnabled, results);
    }
}
