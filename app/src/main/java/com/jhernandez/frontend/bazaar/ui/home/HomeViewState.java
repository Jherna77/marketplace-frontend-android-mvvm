package com.jhernandez.frontend.bazaar.ui.home;


/*
 * Represents the state of the home view in the Bazaar app.
 * Contains information about loading status, user authentication, and favorite items.
 */
public record HomeViewState(Boolean isLoading, Boolean isAuthenticated, Boolean hasFavourites) {

    public HomeViewState withLoading(Boolean loading) {
        return new HomeViewState(loading, isAuthenticated, hasFavourites);
    }

    public HomeViewState withFavourites(Boolean favourites) {
        return new HomeViewState(isLoading, isAuthenticated, favourites);
    }

}
