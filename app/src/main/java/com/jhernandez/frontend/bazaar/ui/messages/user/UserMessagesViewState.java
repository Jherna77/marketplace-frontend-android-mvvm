package com.jhernandez.frontend.bazaar.ui.messages.user;

/*
 * View state for UserMessagesFragment.
 * Contains filter state and whether there are results.
 */
public record UserMessagesViewState(Boolean filterEnabled, Boolean hasResults) {

    public UserMessagesViewState withFilter(Boolean filter) {
        return new UserMessagesViewState(filter, hasResults);
    }

    public UserMessagesViewState withResults(Boolean results) {
        return new UserMessagesViewState(filterEnabled, results);
    }

}
