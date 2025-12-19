package com.jhernandez.frontend.bazaar.ui.review.manage;

/*
 * ViewState for managing the state of the manage review screen.
 * It tracks the loading state.
 */
public record ManageReviewViewState(Boolean isLoading) {

    public ManageReviewViewState withLoading(Boolean loading) {
        return new ManageReviewViewState(loading);
    }
}
