package com.jhernandez.frontend.bazaar.ui.product.search;

/*
 * ViewState for managing the state of the product search screen.
 * It tracks loading state, whether there are results, and if the filter is enabled.
 */
public record SearchProductViewState(Boolean isLoading, Boolean hasResults, Boolean filterEnabled) {

    public SearchProductViewState withLoading(Boolean loading) {
        return new SearchProductViewState(loading, hasResults, filterEnabled);
    }

    public SearchProductViewState withResults(Boolean results) {
        return new SearchProductViewState(isLoading, results, filterEnabled);
    }

    public SearchProductViewState withFilter(Boolean filter) {
        return new SearchProductViewState(isLoading, hasResults, filter);
    }

}
