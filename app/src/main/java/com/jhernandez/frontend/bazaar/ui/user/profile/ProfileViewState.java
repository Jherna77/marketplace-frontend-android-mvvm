package com.jhernandez.frontend.bazaar.ui.user.profile;

/*
 * View state for ProfileFragment.
 * Holds the current state of the profile view, including admin and shop status.
 */
public record ProfileViewState (Boolean isAdmin, Boolean isShop) {

    public ProfileViewState withAdmin(Boolean admin) {
        return new ProfileViewState(admin, isShop);
    }

    public ProfileViewState withIsShop(Boolean shop) {
        return new ProfileViewState(isAdmin, shop);
    }
}
