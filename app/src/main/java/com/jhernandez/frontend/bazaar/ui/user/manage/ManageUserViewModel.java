package com.jhernandez.frontend.bazaar.ui.user.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ADMIN;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRoleRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * ViewModel for ManageUserActivity.
 * Handles user data management, validation, and navigation events related to managing a user's details.
 */
public class ManageUserViewModel extends BaseViewModel {

    private final UserRepositoryPort userRepository;
    private final UserRoleRepositoryPort userRoleRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final SessionRepositoryPort sessionRepository;
    private final MutableLiveData<ManageUserViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<UserRole> userRole = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _enableUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _disableUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();
    private final MutableLiveData<List<UserRole>> _showUserRoleDialog = new MutableLiveData<>();
    private final MutableLiveData<ViewUtils.SelectionDialogData<Category>> categoryDialogData = new MutableLiveData<>();
    private final MutableLiveData<String> _updateCategoriesInfo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToLogin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToMain = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAdmin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goToAccount = new MutableLiveData<>();
    private List<UserRole> allUserRoles;
    private List<Category> allCategories;
    private List<Category> favCategories;

    public ManageUserViewModel(UserRepositoryPort userRepository, UserRoleRepositoryPort userRoleRepository,
                               CategoryRepositoryPort categoryRepository, SessionRepositoryPort sessionRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.categoryRepository = categoryRepository;
        this.sessionRepository = sessionRepository;
    }

    public LiveData<ManageUserViewState> getViewState() {
        return viewState;
    }
    public LiveData<User> getUser() {
        return user;
    }
    public LiveData<UserRole> getUserRole() {
        return userRole;
    }
    public LiveData<Boolean> enableUserEvent() {
        return _enableUser;
    }
    public LiveData<Boolean> disableUserEvent() {
        return _disableUser;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<ValidationError> getValidationError() {
        return validationError;
    }
    public LiveData<List<UserRole>> showUserRoleDialogEvent() {
        return _showUserRoleDialog;
    }
    public LiveData<ViewUtils.SelectionDialogData<Category>> showCategoryDialogEvent() {
        return categoryDialogData;
    }
    public LiveData<String> updateCategoriesInfoEvent() {
        return _updateCategoriesInfo;
    }
    public LiveData<Boolean> goToLoginEvent() {
        return _goToLogin;
    }
    public LiveData<Boolean> goToMainEvent() {
        return _goToMain;
    }
    public LiveData<Boolean> goToAdminEvent() {
        return _goToAdmin;
    }
    public LiveData<Boolean> goToAccountEvent() {
        return _goToAccount;
    }

    public void setViewState(Long userId) {
        Boolean isUpdate = !Objects.equals(userId, NO_ARG);
        Boolean isAdmin = sessionRepository.isAdmin();
        Boolean isSelf = sessionRepository.getSessionUser() != null &&
                Objects.equals(sessionRepository.getSessionUser().id(), userId);
        viewState.setValue( new ManageUserViewState(
                isUpdate,
                isAdmin,
                isSelf,
                false,
                true
        ));

        if (isUpdate) { findUserById(userId); }
        else { favCategories = new ArrayList<>(); }
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateEnabled(Boolean enabled) {
        viewState.setValue(viewState.getValue().withEnabled(enabled));
    }

    private void createUser(User user) {
        showLoading(true);
        Log.d("ManageUserViewModel", "Registering user with email " + user.email());
        userRepository.createUser(user, successCallback(
                () -> {
                    if (!viewState.getValue().isAdmin()) {
                        login(user.email(), user.password());
                    } else {
                        _goToAdmin.postValue(true);
                        showLoading(false);
                    }
                },
                error -> showLoading(false)
        ));
    }

    private void login(String email, String password) {
        Log.d("ManageUserViewModel", "Logging in user with email " + email);
        sessionRepository.login(email, password, new SuccessCallback() {
            @Override
            public void onSuccess() {
                loadSessionUser(email);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                success.postValue(false);
                showLoading(false);
            }
        });
    }

    // Load current session user data from the server
    private void loadSessionUser(String email) {
        Log.d("ManageUserViewModel", "Loading from server user: " + email);
        userRepository.findUserByEmail(email, new TypeCallback<>() {
            @Override
            public void onSuccess(User user) {
                sessionRepository.saveSessionUser(user);
                handleBack();
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                showLoading(false);
            }
        });
    }

    private void findUserById(Long id) {
        showLoading(true);
        Log.d("ManageUserViewModel", "Loading user with id " + id);
        userRepository.findUserById(id, typeCallback(
                user,
                userResult -> {
                        userRole.postValue(userResult.role());
                        favCategories = userResult.favCategories();
                        setCategoriesInfo();
                        updateEnabled(userResult.enabled());
                        showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    private void findAllUserRoles() {
        showLoading(true);
        Log.d("ManageUserViewModel", "Loading user roles...");
        userRoleRepository.findAllUserRoles(new TypeCallback<>() {
            @Override
            public void onSuccess(List<UserRole> userRolesResult) {
                if (!viewState.getValue().isAdmin()) {
                    userRolesResult.removeIf(role -> role.name().equals(ADMIN));
                }
                allUserRoles = userRolesResult;
                _showUserRoleDialog.postValue(userRolesResult);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                apiError.postValue(error);
                showLoading(false);
            }
        });
    }

    private void findAllEnabledCategories() {
        showLoading(true);
        Log.d("ManageProductViewModel", "Loading all enabled categories");
        categoryRepository.findAllEnabledCategories(new TypeCallback<>() {
            @Override
            public void onSuccess(List<Category> allCategoriesResult) {
                allCategories = allCategoriesResult;
                setCategoryDialogData(allCategoriesResult);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                showLoading(false);
            }
        });
    }

    private void updateUser(User user) {
        showLoading(true);
        Log.d("ManageUserViewModel", "Updating user with id " + user.id());
        userRepository.updateUser(user, successCallback(
                () -> {
                    if (viewState.getValue().isSelf()) { sessionRepository.saveSessionUser(user); }
                    handleBack();
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    private void enableUserById(Long id) {
        showLoading(true);
        Log.d("ManageUserViewModel", "Enabling user with id " + id);
        userRepository.enableUserById(id, successCallback(
                () -> {
                    handleBack();
                    showLoading(false);
                },
                error -> showLoading(false)
        ));;
    }

    private void disableUserById(Long id) {
        showLoading(true);
        Log.d("ManageUserViewModel", "Disabling user with id " + id);
        userRepository.disableUserById(id, successCallback(
                () -> {
                    handleBack();
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void validateFields(String email, String password, String confirmPassword, String name,
                               String surnames, String address, String city, String province,
                               String zipCode, Boolean termsChecked) {
        if (userRole.getValue() == null) {
            validationError.setValue(ValidationError.ROLE_NOT_SELECTED);
        } else if (favCategories == null || favCategories.isEmpty()) {
            validationError.setValue(ValidationError.CATEGORY_NOT_SELECTED);
        } else if (email.isEmpty() || (!viewState.getValue().isUpdate() && password.isEmpty()) ||
                name.isEmpty() || surnames.isEmpty() || address.isEmpty() || city.isEmpty() ||
                province.isEmpty() || zipCode.isEmpty()) {
            validationError.setValue(ValidationError.FIELD_EMPTY);
        } else if (!viewState.getValue().isUpdate() && !password.equals(confirmPassword)) {
            validationError.setValue(ValidationError.PASSWORDS_DO_NOT_MATCH);
        } else if (!viewState.getValue().isUpdate() && !viewState.getValue().isAdmin() && !termsChecked) {
            validationError.setValue(ValidationError.TERMS_NOT_ACCEPTED);
        } else {
            validationError.setValue(null);
            submitUser(email, password, name, surnames, address, city, province, zipCode);
        }
    }

    private void submitUser(String email, String password, String name, String surnames,
                            String address, String city, String province, String zipCode) {
        if (viewState.getValue().isUpdate()) {
            User existing = user.getValue();
            updateUser(new User(
                    existing.id(), existing.enabled(), userRole.getValue(), email, password, name, surnames,
                    address, city, province, zipCode, favCategories));
        } else {
            createUser(new User(
                    null, true, userRole.getValue(), email, password, name, surnames, address, city,
                    province, zipCode, favCategories));
        }
    }

    public void onEnableUserSelected() {
        _enableUser.setValue(true);
    }

    public void onEnableUserConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { enableUserById(user.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onDisableUserSelected() {
        _disableUser.setValue(true);
    }

    public void onDisableUserConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { disableUserById(user.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onSelectUserRoleClicked() {
        if (allUserRoles == null) { findAllUserRoles(); }
        else {
            _showUserRoleDialog.postValue(allUserRoles);
        }
    }

    public void onUserRoleSelected(UserRole newUserRole) {
        userRole.setValue(newUserRole);
    }

    public void onSelectCategoriesClicked() {
        if (allCategories == null) { findAllEnabledCategories(); }
        else { setCategoryDialogData(allCategories); }
    }

    public void setCategoryDialogData(List<Category> categories) {
        boolean[] checkedItems = new boolean[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            checkedItems[i] = favCategories.contains(categories.get(i));
        }

        categoryDialogData.setValue(new ViewUtils.SelectionDialogData<>(
                categories,
                categories.stream().map(Category::name).collect(Collectors.toList()),
                checkedItems));
    }

    public void onCategoriesChanged(Category category, Boolean isChecked) {
        if (isChecked) { favCategories.add(category); }
        else {
            favCategories.removeIf(cat ->
                    category.id().equals(cat.id()));
        }
        setCategoriesInfo();
    }

    private void setCategoriesInfo() {
        _updateCategoriesInfo.postValue(TextUtils.join(
                ", ",
                favCategories.stream().map(Category::name).toArray()));
    }

    public void onLoginSelected() {
        _goToLogin.setValue(true);
    }

    public void onGoBackSelected() {
        handleBack();
    }

    public void handleBack() {
        if (viewState.getValue().isAdmin()) { _goToAdmin.setValue(true); }
        else if (viewState.getValue().isUpdate()) { _goToAccount.setValue(true); }
        else { _goToMain.setValue(true); }
    }

}
