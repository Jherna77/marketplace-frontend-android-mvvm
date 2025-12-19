package com.jhernandez.frontend.bazaar.ui.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for handling login logic and state.
 * Manages user authentication and navigation events.
 */
public class LoginViewModel extends BaseViewModel {

    private final SessionRepositoryPort sessionRepository;
    private final UserRepositoryPort userRepository;
    private final MutableLiveData<LoginViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToRegister = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _showSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToMain = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();

    public LoginViewModel(SessionRepositoryPort sessionRepository, UserRepositoryPort userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public LiveData<LoginViewState> getViewState() {
        return viewState;
    }
    public LiveData<Boolean> goToRegisterEvent() {
        return _goToRegister;
    }
    public LiveData<Boolean> showSuccessEvent() {
        return _showSuccess;
    }
    public LiveData<Boolean> goToMainEvent() {
        return _goToMain;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }
    public LiveData<ValidationError> getValidationError() {
        return validationError;
    }

    public void setViewState() {
        viewState.setValue(new LoginViewState(false));
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    public void login(String email, String password) {
        showLoading(true);
        Log.d("LoginViewModel", "Logging in user with email " + email);
        sessionRepository.login(email, password, successCallback(
                        () -> {
                            showLoading(false);
                            loadSessionUser(email);
                            },
                        error -> {
                            showLoading(false);
                        }));
    }

    // Load current session user data from the server
    public void loadSessionUser(String email) {
        showLoading(true);
        Log.d("LoginViewModel", "Loading from server user: " + email);
        userRepository.findUserByEmail(email, new TypeCallback<>() {
            @Override
            public void onSuccess(User user) {
                sessionRepository.saveSessionUser(user);
                _showSuccess.postValue(true);
                _goToMain.postValue(true);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                showLoading(false);
            }
        });
    }

    public void validateFields(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) { validationError.setValue(ValidationError.FIELD_EMPTY); }
        else {
            validationError.setValue(null);
            login(email, password);
        }
    }

    public void onGoToRegisterSelected() {
        _goToRegister.setValue(true);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
