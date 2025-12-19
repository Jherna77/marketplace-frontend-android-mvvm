package com.jhernandez.frontend.bazaar.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityLoginBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.user.manage.ManageUserActivity;


/*
 * Activity representing the LoginActivity.
 * Handles user login functionality.
 * Uses a ViewModel to manage the data and business logic.
 * Uses data binding to bind the UI components to the data.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private AlertDialog progressDialog;

    public static Intent create(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                .get(LoginViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState();
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(
                this,
                getString(R.string.access_title));
    }

    private void initListeners() {
        binding.btnLogin.setOnClickListener(v -> login());
        binding.register.setOnClickListener(v -> viewModel.onGoToRegisterSelected());
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);
        viewModel.goToRegisterEvent().observe(this, go -> goToRegister());
        viewModel.showSuccessEvent().observe(this, this::onSuccessToast);
        viewModel.goToMainEvent().observe(this, go -> goToMain());
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getValidationError().observe(this, this::onValidationError);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(LoginViewState viewState) {
        binding.header.title.setText(R.string.login_title);
        if (viewState.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
    }

    private void login() {
        viewModel.validateFields(
                binding.etEmail.getText().toString().trim(),
                binding.etPassword.getText().toString().trim()
        );
    }

    private void goToRegister() {
        startActivity(ManageUserActivity.create(this));
        finish();
    }

    private void onSuccessToast(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onValidationError(ValidationError error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void goToMain() {
        startActivity(MainActivity.create(this));
        finishAffinity();
    }

    private void goBack() {
        finish();
    }

}