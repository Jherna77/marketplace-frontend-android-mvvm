package com.jhernandez.frontend.bazaar.ui.launcher;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.core.event.Event;
import com.jhernandez.frontend.bazaar.domain.callback.TokenCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for the LauncherActivity.
 * Manages the app's initial setup, including session validation and error handling.
 */
public class LauncherViewModel extends BaseViewModel {

    private final SessionRepositoryPort sessionRepository;
    private final UserRepositoryPort userRepository;
    private final MutableLiveData<LauncherViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> sessionReady = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> _showSnack = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> _restartBazaar = new MutableLiveData<>();


    public LauncherViewModel(SessionRepositoryPort sessionRepository, UserRepositoryPort userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public LiveData<LauncherViewState> getViewState() {
        return viewState;
    }
    public LiveData<Event<Boolean>> isSessionReady() {
        return sessionReady;
    }
    public LiveData<Event<Boolean>> showSnackEvent() {
        return _showSnack;
    }
    public LiveData<Event<Boolean>> restartBazaarEvent() {
        return _restartBazaar;
    }

    public void onCountDownFinished() {
        init();
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void showErrors(Boolean hasErrors) {
        viewState.setValue(viewState.getValue().withError(hasErrors));
    }

    // Validate token from the server
    public void init() {
        viewState.setValue(new LauncherViewState(true, false));
        Log.d("LauncherViewModel", "Validating token...");
        sessionRepository.init(new TokenCallback() {
            @Override
            public void onTokenValid() {
                sessionReady.postValue(new Event<>(true));
                apiError.postValue(null);
                showErrors(false);
                showLoading(false);
            }

            @Override
            public void onTokenExpired() {
                logout();
                sessionReady.postValue(new Event<>(true));
                apiError.postValue(null);
                showErrors(false);
                showLoading(false);
            }

            @Override
            public void onTokenNotFound() {
                sessionReady.postValue(new Event<>(true));
                apiError.postValue(null);
                showErrors(false);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue((error));
                sessionReady.postValue(new Event<>(false));
                _showSnack.postValue(new Event<>(true));
                showErrors(true);
                showLoading(false);
            }
        });
    }

    public void logout() {
        Log.d("LauncherViewModel", "Logging out user...");
        sessionRepository.logout();
    }

    public void onRestartSelected() {
        _restartBazaar.postValue(new Event<>(true));
    }

}
