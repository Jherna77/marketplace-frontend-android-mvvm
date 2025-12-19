package com.jhernandez.frontend.bazaar.domain.callback;

/*
 * Callback interface for confirmation results.
 */
public interface ConfirmationCallback {
    void onResult(boolean isConfirmed);
}
