package com.jhernandez.frontend.bazaar.ui.category.admin;

/*
 * View state for AdminCategoriesFragment.
 * Holds the UI state related to category administration, such as filter status.
 */
public record AdminCategoriesViewState(Boolean filterEnabled) {

    public AdminCategoriesViewState withFilter(Boolean filter) {
        return new AdminCategoriesViewState(filter);
    }

}
