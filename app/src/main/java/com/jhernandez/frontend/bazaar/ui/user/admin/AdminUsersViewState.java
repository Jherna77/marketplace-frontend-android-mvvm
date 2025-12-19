package com.jhernandez.frontend.bazaar.ui.user.admin;

/*
 * View state for AdminUsers screen.
 * Holds the current state of filters applied to the user list.
 */
public record AdminUsersViewState(Boolean filterEnabled) {

    public AdminUsersViewState withFilter(Boolean filter) {
        return new AdminUsersViewState(filter);
    }
}
