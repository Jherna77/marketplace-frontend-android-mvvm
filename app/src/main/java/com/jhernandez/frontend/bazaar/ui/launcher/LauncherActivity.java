package com.jhernandez.frontend.bazaar.ui.launcher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityLauncherBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.ui.common.util.ErrorUtils;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;


/*
 * Activity representing the launcher screen of the Bazaar app.
 * Displays a splash screen and handles initial app setup.
 */
public class LauncherActivity extends AppCompatActivity {

    private ActivityLauncherBinding binding;
    private LauncherViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        initViewModel();
        showSplashScreen();
        initObservers();
    }

    private void showSplashScreen() {
        Log.d("LauncherActivity", "Showing SplashScreen");
        new CountDownTimer(3800, 200) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                viewModel.onCountDownFinished();
            }
        }.start();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(LauncherViewModel.class);
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);
        viewModel.isSessionReady().observe(this, event -> {
            Boolean isSessionReady = event != null ? event.getContentIfNotHandled() : null;
            if (Boolean.TRUE.equals(isSessionReady)) goToMain();
        });
        viewModel.showSnackEvent().observe(this, event -> {
            Boolean showSnack = event != null ? event.getContentIfNotHandled() : null;
            if (Boolean.TRUE.equals(showSnack)) showSnack();
        });
        viewModel.restartBazaarEvent().observe(this, event -> {
            Boolean restartBazaar = event != null ? event.getContentIfNotHandled() : null;
            if (Boolean.TRUE.equals(restartBazaar)) restartApp();
        });
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(LauncherViewState viewState) {
        binding.splash.setVisibility(viewState.isLoading() ? View.VISIBLE : View.GONE);
        binding.errorImg.setVisibility(viewState.hasErrors() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        if (error != null) {
            binding.errorTitle.setText(ErrorUtils.getMessage(this, error.getApiError()));
            binding.errorMessage.setText(ErrorUtils.getMessage(this, error));
            ViewUtils.rumbleElement(binding.errorMessage);
        }
    }

    private void goToMain() {
        startActivity(MainActivity.create(this));
        finish();
    }

    private void showSnack() {
        Snackbar.make(binding.getRoot(),
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.restart_app, v -> viewModel.onRestartSelected())
                .show();
    }

    @SuppressLint("UnsafeIntentLaunch")
    private void restartApp() {
        Log.d("BaseActivity", "Restarting app...");
        finishAffinity();
        startActivity(getIntent());
    }
}