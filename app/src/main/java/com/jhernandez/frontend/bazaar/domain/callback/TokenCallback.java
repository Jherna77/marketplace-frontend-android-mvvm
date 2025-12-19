package com.jhernandez.frontend.bazaar.domain.callback;

import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;

/*
 * Callback interface for token validation results.
 */
public interface TokenCallback {
    void onTokenValid();

    void onTokenExpired();

    void onTokenNotFound();

    void onError(ApiErrorResponse error);
}
