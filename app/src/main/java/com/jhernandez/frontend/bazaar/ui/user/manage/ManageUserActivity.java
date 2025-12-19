package com.jhernandez.frontend.bazaar.ui.user.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_USER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityManageUserBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;
import com.jhernandez.frontend.bazaar.ui.admin.AdminActivity;
import com.jhernandez.frontend.bazaar.ui.auth.LoginActivity;
import com.jhernandez.frontend.bazaar.ui.common.util.UserRoleUtils;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.user.account.AccountActivity;

import java.util.List;

/*
 * Activity for managing a user's details.
 * It handles displaying user information, editing, enabling/disabling, and navigation.
 */
public class ManageUserActivity extends AppCompatActivity {

    public static Intent create(Context context) {
        return new Intent(context, ManageUserActivity.class);
    }

    public static Intent create(Context context, Long id) {
        return new Intent(context, ManageUserActivity.class).putExtra(ARG_USER, id);
    }

    private ActivityManageUserBinding binding;
    private ManageUserViewModel viewModel;
    private Long userId;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = getIntent().getLongExtra(ARG_USER, NO_ARG);
        initUI();
    }

    private void initUI() {
        initViewModel();
        initViewState();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ManageUserViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState(userId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.tvFavouriteCategories.setOnClickListener(v -> viewModel.onSelectCategoriesClicked());
        binding.tvUserRole.setOnClickListener(v -> viewModel.onSelectUserRoleClicked());
        binding.btnSaveUser.setOnClickListener(v -> saveUser());
        binding.btnEnableUser.setOnClickListener(v -> viewModel.onEnableUserSelected());
        binding.btnDisableUser.setOnClickListener(v -> viewModel.onDisableUserSelected());
        binding.logIn.setOnClickListener(v -> viewModel.onLoginSelected());
        binding.tvTerms.setOnClickListener(v -> showTerms());
        binding.tvPrivacy.setOnClickListener(v -> showPrivacy());
        binding.tvAbout.setOnClickListener(v -> showAbout());
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);//userViewState -> {
        viewModel.getUser().observe(this, this::setUserInfo);
        viewModel.showUserRoleDialogEvent().observe(this, this::showUserRoleDialog);
        viewModel.getUserRole().observe(this, this::updateUserRoleInfo);
        viewModel.showCategoryDialogEvent().observe(this, this::showCategoryDialog);
        viewModel.updateCategoriesInfoEvent().observe(this, this::updateCategoriesInfo);
        viewModel.enableUserEvent().observe(this, this::enableUserConfirmation);
        viewModel.disableUserEvent().observe(this, this::disableUserConfirmation);
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.isCancelledAction().observe(this, this::onCancelledAction);
        viewModel.goToLoginEvent().observe(this, go -> goToLogin());
        viewModel.goToMainEvent().observe(this, go -> goToMain());
        viewModel.goToAccountEvent().observe(this, go -> goToAccount());
        viewModel.goToAdminEvent().observe(this, go -> goToAdmin());
        viewModel.getValidationError().observe(this, this::onValidationError);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(ManageUserViewState viewState) {
        if (viewState.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
        binding.header.title.setText(getString(viewState.isUpdate() ? R.string.title_update : R.string.title_register));
        binding.btnSaveUser.setText(getString(viewState.isUpdate() ? R.string.update_user_btn : R.string.register_btn));
        binding.btnSaveUser.setVisibility(viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.btnSaveUser.setEnabled(!viewState.isLoading());
        binding.btnEnableUser.setVisibility(!viewState.isEnabled() && viewState.isUpdate() && viewState.isAdmin() ? View.VISIBLE : View.GONE);
        binding.btnEnableUser.setEnabled(!viewState.isLoading());
        binding.btnDisableUser.setVisibility(viewState.isEnabled() && viewState.isUpdate() && viewState.isAdmin() && !viewState.isSelf() ? View.VISIBLE : View.GONE);
        binding.btnDisableUser.setEnabled(!viewState.isLoading());
        binding.tilPassword.setVisibility(!viewState.isUpdate() ? View.VISIBLE : View.GONE);
        binding.tilConfirmPw.setVisibility(!viewState.isUpdate() ? View.VISIBLE : View.GONE);
        binding.pwInfo.setVisibility(!viewState.isUpdate() ? View.VISIBLE : View.GONE);
        binding.llTerms.setVisibility(viewState.isUpdate() || viewState.isAdmin() ? View.GONE : View.VISIBLE);
        binding.cbTerms.setChecked(viewState.isUpdate() || viewState.isAdmin());
        binding.logIn.setVisibility(!viewState.isUpdate() && !viewState.isAdmin() ? View.VISIBLE : View.GONE);
        binding.etEmail.setEnabled(!viewState.isUpdate());
        binding.tvUserRole.setEnabled(viewState.isEnabled() && !viewState.isLoading());
        binding.name.setEnabled(viewState.isEnabled());
        binding.surnames.setEnabled(viewState.isEnabled());
        binding.address.setEnabled(viewState.isEnabled());
        binding.city.setEnabled(viewState.isEnabled());
        binding.province.setEnabled(viewState.isEnabled());
        binding.zipCode.setEnabled(viewState.isEnabled());
    }

    private void setUserInfo(User user) {
        binding.tvUserRole.setText(UserRoleUtils.getLabel(this, user.role()));
        binding.etEmail.setText(user.email());
        binding.name.setText(user.name());
        binding.surnames.setText(user.surnames());
        binding.address.setText(user.address());
        binding.city.setText(user.city());
        binding.province.setText(user.province());
        binding.zipCode.setText(user.zipCode());
    }

    private void showUserRoleDialog(List<UserRole> allUserRoles) {
        String[] roleNames = allUserRoles.stream()
                .map(role -> UserRoleUtils.getLabel(this, role))
                .toArray(String[]::new);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_user_role)
                .setItems(
                        roleNames,
                        (dialog, which) ->
                            viewModel.onUserRoleSelected(allUserRoles.get(which))
                        )
                .show();
    }

    private void updateUserRoleInfo(UserRole userRole) {
        binding.tvUserRole.setText(UserRoleUtils.getLabel(this, userRole));
    }

    private void showCategoryDialog(ViewUtils.SelectionDialogData<Category> data) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_fav_categories)
                .setMultiChoiceItems(
                        data.itemNames().toArray(new String[0]),
                        data.checkedItems(),
                        (dialog, which, isChecked) ->
                                viewModel.onCategoriesChanged(data.items().get(which), isChecked)
                )
                .setPositiveButton(R.string.ok_resp, null)
                .show();
    }

    private void updateCategoriesInfo(String categories) {
        binding.tvFavouriteCategories.setText(categories);
    }

    private void saveUser() {
        viewModel.validateFields(
                binding.etEmail.getText().toString().trim(),
                binding.etPassword.getText().toString().trim(),
                binding.confirmPw.getText().toString().trim(),
                binding.name.getText().toString().trim(),
                binding.surnames.getText().toString().trim(),
                binding.address.getText().toString().trim(),
                binding.city.getText().toString().trim(),
                binding.province.getText().toString().trim(),
                binding.zipCode.getText().toString().trim(),
                binding.cbTerms.isChecked()
        );
    }

    private void disableUserConfirmation(Boolean show) {
        if (show) {
            ViewUtils.confirmActionDialog(
                    this,
                    getString(R.string.disable_user_warn),
                    getString(R.string.disable_user_msg),
                    viewModel::onDisableUserConfirmation);
        }
    }

    private void enableUserConfirmation(Boolean show) {
        if (show) {
            ViewUtils.confirmActionDialog(
                    this,
                    getString(R.string.enable_user_warn),
                    getString(R.string.enable_user_msg),
                    viewModel::onEnableUserConfirmation);
        }
    }

    private void onSuccess(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(this, cancelled);
    }

    private void onValidationError(ValidationError error) {
        if (error != null) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    private void onApiError(ApiErrorResponse error) {
        if (error != null) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    // Show terms and conditions
    private void showTerms() {
        ViewUtils.showTerms(this);
    }

    // Show privacy policy
    private void showPrivacy() {
        ViewUtils.showPrivacy(this);
    }

    // Show about BaZaaR...
    private void showAbout() {
        ViewUtils.showAbout(this);
    }

    private void goToMain() {
        startActivity(MainActivity.create(this));
        finish();
    }

    private void goToAccount() {
        startActivity(AccountActivity.create(this));
        finish();
    }

    private void goToAdmin() {
        startActivity(AdminActivity.create(this, ARG_USER));
        finish();
    }

    private void goToLogin() {
        startActivity(LoginActivity.create(this));
        finish();
    }

}
