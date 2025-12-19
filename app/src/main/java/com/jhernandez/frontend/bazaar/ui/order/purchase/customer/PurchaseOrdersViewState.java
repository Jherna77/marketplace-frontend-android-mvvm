package com.jhernandez.frontend.bazaar.ui.order.purchase.customer;

/*
 * View state for PurchaseOrdersActivity.
 * Contains filter state and whether there are results.
 */
public record PurchaseOrdersViewState(Boolean filterEnabled, Boolean hasResults) {

    public PurchaseOrdersViewState withFilter(Boolean filter) {
        return new PurchaseOrdersViewState(filter, hasResults);
    }

    public PurchaseOrdersViewState withResults(Boolean results) {
        return new PurchaseOrdersViewState(filterEnabled, results);
    }

}
