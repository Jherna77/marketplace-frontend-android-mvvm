package com.jhernandez.frontend.bazaar.data.network;

import android.util.Log;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiError;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;

import java.util.function.Consumer;

import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Delegator class for handling Retrofit callbacks and delegating responses to appropriate callbacks.
 */
public class CallbackDelegator {

    public static <T> Callback<T> delegate(String tag, TypeCallback<T> callback) {
        return new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    Log.d(tag, "Request successful");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(tag, "Request error: " + response.code());
                    callback.onError(parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e(tag, "Request failed: " + t.getMessage());
                callback.onError(parseError(t));
            }
        };
    }

    public static <T> Callback<T> delegate(String tag, SuccessCallback callback) {
        return new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    Log.d(tag, "Request successful");
                    callback.onSuccess();
                } else {
                    Log.e(tag, "Request error: " + response.code());
                    callback.onError(parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e(tag, "Request failed: " + t.getMessage());
                callback.onError(parseError(t));
            }
        };
    }

    public static <T> Callback<T> delegate(String tag, Consumer<T> onSuccess, Consumer<ApiErrorResponse> onError) {
        return new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    Log.d(tag, "Request successful");
                    onSuccess.accept(response.body());
                } else {
                    Log.e(tag, "Request error: " + response.code());
                    onError.accept(parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e(tag, "Request failed: " + t.getMessage());
                onError.accept(parseError(t));

            }
        };
    }

    public static <T> ApiErrorResponse parseError(Response<T> response) {
       return new ApiErrorResponse(
                ApiError.fromCode(response.code()),
                JsonUtils.parseErrorResponseBody(response));
    }

    public static <T> ApiErrorResponse parseError(Throwable t) {
       return new ApiErrorResponse(
               ApiError.NETWORK_ERROR,
               t.getMessage());
    }

}
