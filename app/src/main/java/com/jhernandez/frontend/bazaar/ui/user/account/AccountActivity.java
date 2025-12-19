package com.jhernandez.frontend.bazaar.ui.user.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityAccountBinding;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.user.manage.ManageUserActivity;

/*
 * Activity for displaying and managing the user's account information.
 * It handles displaying user details, navigation to edit user info, and navigation back.
 */
public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private AccountViewModel viewModel;

    public static Intent create(Context context) {
        return new Intent(context, AccountActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(AccountViewModel.class);
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.account_info_title);
        initListeners();
        initObservers();
    }

    private void initListeners() {
        binding.editFab.setOnClickListener(v -> viewModel.onEditUserSelected());
        binding.header.btnBack.setOnClickListener(v -> goBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });
    }

    private void initObservers() {
        viewModel.getUser().observe(this, this::setUserInfo);
        viewModel.getGoToEditUser().observe(this, this::goToEditUser);
    }

    private void setUserInfo(User user) {
        binding.tvEmail.setText(String.format(getString(R.string.email_tv), user.email()));
        binding.tvName.setText(String.format(getString(R.string.name_tv), user.name()));
        binding.tvSurnames.setText(String.format(getString(R.string.surnames_tv), user.surnames()));
        binding.tvAddress.setText(String.format(getString(R.string.address_tv), user.address()));
        binding.tvCity.setText(String.format(getString(R.string.city_tv), user.city()));
        binding.tvProvince.setText(String.format(getString(R.string.province_tv), user.province()));
        binding.tvZipCode.setText(String.format(getString(R.string.zip_code_tv), user.zipCode()));
    }

    private void goToEditUser(Long userId) {
        startActivity(ManageUserActivity.create(this, userId));
        finish();
    }

    private void goBack() {
        startActivity(MainActivity.create(this));
        finish();
    }

}