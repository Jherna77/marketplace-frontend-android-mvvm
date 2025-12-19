package com.jhernandez.frontend.bazaar.domain.callback;

import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;

/*
 * Callback interface for success and error handling.
 */
public interface SuccessCallback {
    void onSuccess();

    void onError(ApiErrorResponse error);
}