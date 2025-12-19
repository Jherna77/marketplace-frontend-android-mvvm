package com.jhernandez.frontend.bazaar.ui.launcher;


/*
 * Represents the state of the launcher view in the Bazaar app.
 * Contains information about loading status and error presence.
 */
public record LauncherViewState(Boolean isLoading, Boolean hasErrors) {

    public LauncherViewState withLoading(Boolean loading) {
        return new LauncherViewState(loading, hasErrors);
    }

    public LauncherViewState withError(Boolean errors) {
        return new LauncherViewState(isLoading, errors);
    }
}
