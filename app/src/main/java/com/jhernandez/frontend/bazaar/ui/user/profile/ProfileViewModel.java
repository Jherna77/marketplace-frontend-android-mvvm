package com.jhernandez.frontend.bazaar.ui.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for ProfileFragment.
 * Manages user profile data and navigation events related to the user's profile.
 */
public class ProfileViewModel extends BaseViewModel {

    private final SessionRepositoryPort sessionRepository;
    private final MutableLiveData<ProfileViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAccount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToOrders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToFavourites = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToReviews = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToShop = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAdmin = new MutableLiveData<>();

    public ProfileViewModel(SessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.user.setValue(sessionRepository.getSessionUser());
        setViewState();
    }

    public LiveData<ProfileViewState> getViewState() {
        return viewState;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> goToAccountEvent() {
        return _goToAccount;
    }

    public LiveData<Boolean> goToOrdersEvent() {
        return _goToOrders;
    }

    public LiveData<Boolean> goToFavouritesEvent() {
        return _goToFavourites;
    }

    public LiveData<Boolean> goToReviewsEvent() {
        return _goToReviews;
    }

    public LiveData<Boolean> goToShopEvent() {
        return _goToShop;
    }

    public LiveData<Boolean> goToAdminEvent() {
        return _goToAdmin;
    }


    private void setViewState() {
        viewState.setValue(new ProfileViewState(
                sessionRepository.isAdmin(),
                sessionRepository.isShop())
        );
    }

    public void onAccountSelected() {
        _goToAccount.setValue(true);
    }

    public void onOrdersSelected() {
        _goToOrders.setValue(true);
    }

    public void onFavouritesSelected() {
        _goToFavourites.setValue(true);
    }

    public void onReviewsSelected() {
        _goToReviews.setValue(true);
    }

    public void onShopSelected() {
        _goToShop.setValue(true);
    }

    public void onAdminSelected() {
        _goToAdmin.setValue(true);
    }


}
