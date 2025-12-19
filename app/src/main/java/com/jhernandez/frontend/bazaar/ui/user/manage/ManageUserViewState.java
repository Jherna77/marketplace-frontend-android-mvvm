package com.jhernandez.frontend.bazaar.ui.user.manage;

/*
 * View state for ManageUser screen.
 * Holds the current state of the user management view, including update status, admin status, self status, loading state, and enabled state.
 */
public record ManageUserViewState(Boolean isUpdate, Boolean isAdmin, Boolean isSelf, Boolean isLoading, Boolean isEnabled) {

    public ManageUserViewState withLoading(Boolean loading) {
        return new ManageUserViewState(isUpdate, isAdmin, isSelf, loading, isEnabled);
    }

    public ManageUserViewState withEnabled(Boolean enabled) {
        return new ManageUserViewState(isUpdate, isAdmin, isSelf, isLoading, enabled);
    }
}
