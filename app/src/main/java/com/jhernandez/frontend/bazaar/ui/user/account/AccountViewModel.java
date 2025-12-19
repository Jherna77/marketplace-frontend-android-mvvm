package com.jhernandez.frontend.bazaar.ui.user.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for AccountActivity.
 * Manages user account data and navigation events related to the user's account.
 */
public class AccountViewModel extends BaseViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToEditUser = new MutableLiveData<>();

    public AccountViewModel(SessionRepositoryPort sessionRepository) {
        this.user.setValue(sessionRepository.getSessionUser());
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Long> getGoToEditUser() {
        return _goToEditUser;
    }

    public void onEditUserSelected() {
        _goToEditUser.setValue(user.getValue().id());
    }

}
