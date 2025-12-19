package com.jhernandez.frontend.bazaar.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.port.MessageRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for MainActivity.
 * Manages user session state and message notifications.
 */
public class MainViewModel extends BaseViewModel {

    private final SessionRepositoryPort sessionRepository;
    private final MessageRepositoryPort messageRepository;
    private final MutableLiveData<MainViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToHome = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToLogin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _exit = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();

    public MainViewModel(SessionRepositoryPort sessionRepository, MessageRepositoryPort messageRepository) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
        initViewState();
    }

    public LiveData<MainViewState> getViewState() {
        return viewState;
    }
    public LiveData<Boolean> goToHomeEvent() {
        return _goToHome;
    }
    public LiveData<Boolean> goToLoginEvent() {
        return _goToLogin;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<Boolean> exitEvent() {
        return _exit;
    }

    public void initViewState() {
        Boolean isAuthenticated = sessionRepository.isAuthenticated();
        viewState.setValue(new MainViewState(
                isAuthenticated,
                false));

        if (isAuthenticated) {
            findNewMessages(sessionRepository.getSessionUser().id()); }
    }

    private void updateAuthenticated(boolean authenticated) {
        viewState.setValue(viewState.getValue().withAuthenticated(authenticated));
    }

    private void updateNewMessages(boolean newMessages) {
        viewState.setValue(viewState.getValue().withNewMessages(newMessages));
    }

    private void findNewMessages(Long recipientId) {
        Log.d("MainViewModel", "Finding new messages...");
        messageRepository.hasNewMessages(recipientId, new TypeCallback<>() {
            @Override
            public void onSuccess(Boolean hasNewMessages) {
                updateNewMessages(hasNewMessages);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                updateNewMessages(false);
            }
        });
    }

    public void onLogoClicked() {
        _goToHome.setValue(true);
    }

    public void onLoginSelected() {
        _goToLogin.setValue(true);
    }

    public void onLogoutConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { logout(); }
        else { onCancelActionSelected(); }
    }

    private void logout() {
        Log.d("SessionViewModel", "Logging out user...");
        sessionRepository.logout();
        updateAuthenticated(false);
        _goToHome.setValue(true);
        success.setValue(true);
    }

    public void onExitConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { _exit.setValue(true); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }
}
