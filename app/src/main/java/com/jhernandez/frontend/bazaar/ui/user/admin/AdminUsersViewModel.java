package com.jhernandez.frontend.bazaar.ui.user.admin;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ALL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ASCENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_DISABLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ENABLED;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for AdminUsersFragment.
 * Manages user data, filtering, and navigation events related to user management in the admin section.
 */
public class AdminUsersViewModel extends BaseViewModel {

    private final UserRepositoryPort userRepository;
    private final MutableLiveData<AdminUsersViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<User>> filteredUsers = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToEditUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAddUser = new MutableLiveData<>();
    private List<User> allUsers;
    private String selectedRole;
    private String selectedStatus;
    private String selectedOrder;

    public AdminUsersViewModel(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<AdminUsersViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<User>> getFilteredUsers() {
        return filteredUsers;
    }
    public LiveData<Long> goToEditUserEvent() {
        return _goToEditUser;
    }
    public LiveData<Boolean> goToAddUserEvent() {
        return _goToAddUser;
    }

    public void initViewState() {
        viewState.setValue(new AdminUsersViewState(false));
        selectedRole = TAG_ALL;
        selectedStatus = TAG_ALL;
        selectedOrder = TAG_ASCENDING;
        findAllUsers();
    }

    private void updateFilter(Boolean filter) {
        viewState.setValue(viewState.getValue().withFilter(filter));
    }

    // Load users from the server
    public void findAllUsers() {
        Log.d("UserViewModel", "Loading all users...");
        userRepository.findAllUsers(new TypeCallback<>() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers = users;
                filteredUsers.postValue(users);
                apiError.postValue(null);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                allUsers = new ArrayList<>();
                filteredUsers.postValue(allUsers);
            }
        });
    }

    public void onEditUserSelected(Long userId) {
        _goToEditUser.setValue(userId);
    }

    public void onAddUserSelected() {
        _goToAddUser.setValue(true);
    }

    public void onFilterResultsSelected() {
        updateFilter(!viewState.getValue().filterEnabled());
    }

    public void onRoleFilterSelected(String role) {
        selectedRole = role;
        applyFilters();
    }

    public void onStatusFilterSelected(String status) {
        selectedStatus = status;
        applyFilters();
    }

    public void onOrderSelected(String order) {
        selectedOrder = order;
        applyFilters();
    }

    private void applyFilters() {
        List<User> filtered = new ArrayList<>();

        for (User user : allUsers) {
            Boolean matchesRole = selectedRole.equals(TAG_ALL) || user.role().name().equalsIgnoreCase(selectedRole);
            Boolean matchesStatus = selectedStatus.equals(TAG_ALL) ||
                    (selectedStatus.equals(TAG_ENABLED) && user.enabled()) ||
                    (selectedStatus.equals(TAG_DISABLED) && !user.enabled());

            if (matchesRole && matchesStatus) { filtered.add(user); }
        }

        if (selectedOrder.equals(TAG_ASCENDING)) {
            filtered.sort(Comparator.comparing(User::name));
        } else {
            filtered.sort(Comparator.comparing(User::name).reversed());        }

        filteredUsers.setValue(filtered);
    }

}
