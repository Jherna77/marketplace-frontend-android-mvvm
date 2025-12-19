package com.jhernandez.frontend.bazaar.ui.auth;


/*
 * ViewState representing the state of the LoginActivity.
 * It holds information about the loading state.
 */
public record LoginViewState(Boolean isLoading) {

    public LoginViewState withLoading(Boolean loading) {
        return new LoginViewState(loading);
    }
}
