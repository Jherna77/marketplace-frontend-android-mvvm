package com.jhernandez.frontend.bazaar.ui.main;


/*
 * Represents the state of the main view in the Bazaar app.
 * Contains information about user authentication and message notifications.
 */
public record MainViewState (Boolean isAuthenticated, Boolean hasNewMessages){

    public MainViewState withAuthenticated(Boolean authenticated) {
        return new MainViewState(authenticated, hasNewMessages);
    }

    public MainViewState withNewMessages(Boolean newMessages) {
        return new MainViewState(isAuthenticated, newMessages);
    }

}
