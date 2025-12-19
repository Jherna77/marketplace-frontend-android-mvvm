package com.jhernandez.frontend.bazaar.ui.main;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CART;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_FRAGMENT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_HOME;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_MESSAGE;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_SEARCH;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.badge.BadgeDrawable;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityMainBinding;
import com.jhernandez.frontend.bazaar.ui.auth.LoginActivity;
import com.jhernandez.frontend.bazaar.ui.cart.CartFragment;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.home.HomeFragment;
import com.jhernandez.frontend.bazaar.ui.messages.user.UserMessagesFragment;
import com.jhernandez.frontend.bazaar.ui.product.search.SearchProductFragment;
import com.jhernandez.frontend.bazaar.ui.user.profile.ProfileFragment;


/*
 * Activity representing the main screen of the Bazaar app.
 * Manages navigation between different sections like home, search, cart, messages, and profile.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private Integer containerViewId;

    public static Intent create(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static Intent create(Context context, String fragment) {
        return new Intent(context, MainActivity.class).putExtra(ARG_FRAGMENT, fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        initViewModel();
        initBottomNavMenu();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(MainViewModel.class);
    }

    private void initBottomNavMenu() {
        containerViewId = R.id.fragment_container_main;
        replaceFragment(containerViewId, getDefaultFragment());
    }

    private void initListeners() {
        binding.headerSession.logo.setOnClickListener(v -> viewModel.onLogoClicked());
        binding.headerSession.btnLogin.setOnClickListener(v -> viewModel.onLoginSelected());
        binding.headerSession.btnLogout.setOnClickListener(v -> logoutConfirmation());
        binding.bottomNavigationMain.setOnItemSelectedListener(item -> {
            manageNavigation(item.getItemId());
            return true;
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitConfirmation();
            }
        });
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);
        viewModel.goToHomeEvent().observe(this, go -> goToHome());
        viewModel.goToLoginEvent().observe(this, go -> goToLogin());
        viewModel.isSuccess().observe(this, this::onSuccessToast);
        viewModel.isCancelledAction().observe(this, this::onCancelledAction);
        viewModel.exitEvent().observe(this, go -> exit());
    }

    private void updateUI(MainViewState state) {
        BadgeDrawable badge = binding.bottomNavigationMain.getOrCreateBadge(R.id.nav_messages);
        badge.setVisible(state.hasNewMessages());

        binding.headerSession.btnLogin.setVisibility(state.isAuthenticated() ? View.GONE : View.VISIBLE);
        binding.headerSession.btnLogout.setVisibility(state.isAuthenticated() ? View.VISIBLE : View.GONE);
    }

    private void manageNavigation(Integer itemId) {
        if ((itemId == R.id.nav_profile || itemId == R.id.nav_cart || itemId == R.id.nav_messages) &&
                (!viewModel.getViewState().getValue().isAuthenticated())) { goToLogin(); }
        else if (itemId == R.id.nav_home) replaceFragment(containerViewId, new HomeFragment());
        else if (itemId == R.id.nav_search) replaceFragment(containerViewId, new SearchProductFragment());
        else if (itemId == R.id.nav_cart) replaceFragment(containerViewId, new CartFragment());
        else if (itemId == R.id.nav_profile) replaceFragment(containerViewId, new ProfileFragment());
        else if (itemId == R.id.nav_messages) replaceFragment(containerViewId, new UserMessagesFragment());
    }

    private void replaceFragment(Integer containerViewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commit();
    }

    private Fragment getDefaultFragment() {
        return (getIntent().hasExtra(ARG_FRAGMENT))
                ? switch (getIntent().getStringExtra(ARG_FRAGMENT)) {
                    case ARG_HOME -> new HomeFragment();
                    case ARG_SEARCH -> new SearchProductFragment();
                    case ARG_CART -> new CartFragment();
                    case ARG_PROFILE -> new ProfileFragment();
                    case ARG_MESSAGE -> new UserMessagesFragment();
                    default ->
                            throw new IllegalStateException("Unexpected value: " + getIntent().getStringExtra(ARG_FRAGMENT));
                }
                : new HomeFragment();
    }

    private void exitConfirmation() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.exit_warn),
                getString(R.string.exit_msg),
                viewModel::onExitConfirmation);
    }

    private void logoutConfirmation() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.logout_warn),
                getString(R.string.logout_msg),
                viewModel::onLogoutConfirmation);
    }

    private void onSuccessToast(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(this, cancelled);
    }

    private void goToHome() {
        startActivity(MainActivity.create(this, ARG_HOME));
        finishAffinity();
    }

    private void goToLogin() {
        startActivity(LoginActivity.create(this));
    }

    private void exit() {
        finishAffinity();
    }

}