package com.jhernandez.frontend.bazaar.domain.callback;

import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;

/*
 * Generic callback interface for handling results of various types.
 */
public interface TypeCallback<T> {
    void onSuccess(T result);

    void onError(ApiErrorResponse error);
}