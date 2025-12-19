package com.jhernandez.frontend.bazaar.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;

import java.util.function.Consumer;

/* Base ViewModel class providing common LiveData and callback handling.
 * It includes LiveData for success status and API errors,
 * along with utility methods to create TypeCallback and SuccessCallback instances
 * that update the LiveData accordingly.
 */
public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    protected final MutableLiveData<ApiErrorResponse> apiError = new MutableLiveData<>();

    public LiveData<Boolean> isSuccess() {
        return success;
    }

    public LiveData<ApiErrorResponse> getApiError() {
        return apiError;
    }

    protected <T> TypeCallback<T> typeCallback(MutableLiveData<T> liveData) {
        return new TypeCallback<T>() {
            @Override
            public void onSuccess(T result) {
                liveData.postValue(result);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                liveData.postValue(null);
            }
        };
    }

    protected <T> TypeCallback<T> typeCallback(MutableLiveData<T> liveData, Consumer<T> onSuccess, Consumer<ApiErrorResponse> onError) {
        return new TypeCallback<>() {
            @Override
            public void onSuccess(T result) {
                liveData.postValue(result);
                apiError.postValue(null);
                if (onSuccess != null) onSuccess.accept(result);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                liveData.postValue(null);
                apiError.postValue(error);
                if (onError != null) onError.accept(error);
            }
        };
    }

    protected SuccessCallback successCallback() {
        return new SuccessCallback() {
            @Override
            public void onSuccess() {
                success.postValue(true);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                success.postValue(false);
            }
        };
    }

    protected SuccessCallback successCallback(Runnable onSuccess, Consumer<ApiErrorResponse> onError) {
        return new SuccessCallback() {
            @Override
            public void onSuccess() {
                success.postValue(true);
                apiError.postValue(null);
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onError(ApiErrorResponse error) {
                success.postValue(false);
                apiError.postValue(error);
                if (onError != null) onError.accept(error);
            }
        };
    }

}
