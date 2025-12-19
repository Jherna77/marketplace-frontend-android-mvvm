package com.jhernandez.frontend.bazaar.ui.messages.detail;

/*
 * View state for MessageDetailActivity.
 * Contains loading state information.
 */
public record MessageDetailViewState(Boolean isLoading) {

    public MessageDetailViewState withLoading(Boolean loading) {
        return new MessageDetailViewState(loading);
    }

}
