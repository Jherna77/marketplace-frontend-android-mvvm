package com.jhernandez.frontend.bazaar.ui.admin;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_BACKUP;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CATEGORY;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_FRAGMENT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ActivityAdminBinding;
import com.jhernandez.frontend.bazaar.ui.backup.admin.BackupFragment;
import com.jhernandez.frontend.bazaar.ui.category.admin.AdminCategoriesFragment;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.product.admin.AdminProductsFragment;
import com.jhernandez.frontend.bazaar.ui.user.admin.AdminUsersFragment;

/*
 * Activity representing the AdminActivity.
 * It contains the bottom navigation menu to access different admin sections.
 */
public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private Integer containerViewId;

    public static Intent create(Context context) {
        return new Intent(context, AdminActivity.class);
    }

    public static Intent create(Context context, String fragment) {
        return new Intent(context, AdminActivity.class).putExtra(ARG_FRAGMENT, fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        initHeader();
        initBottomNavMenu();
        initListeners();
    }

    private void initHeader() {
        binding.header.title.setText(R.string.admin);
    }

    private void initBottomNavMenu() {
        containerViewId = R.id.fragment_container_admin;
        replaceFragment(containerViewId, getDefaultFragment());
    }

    private void initListeners() {
        binding.bottomNavigationAdmin.setOnItemSelectedListener(item -> {
            manageNavigation(item.getItemId());
            return true;
        });
        binding.header.btnBack.setOnClickListener(v -> goBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { goBack(); }
        });
    }

    private void manageNavigation(Integer itemId) {
        if (itemId == R.id.nav_users) replaceFragment(containerViewId,new AdminUsersFragment());
        else if (itemId == R.id.nav_categories) replaceFragment(containerViewId,new AdminCategoriesFragment());
        else if (itemId == R.id.nav_products) replaceFragment(containerViewId,new AdminProductsFragment());
        else if (itemId == R.id.nav_backup) replaceFragment(containerViewId,new BackupFragment());
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
                    case ARG_USER -> new AdminUsersFragment();
                    case ARG_PRODUCT -> new AdminProductsFragment();
                    case ARG_CATEGORY -> new AdminCategoriesFragment();
                    case ARG_BACKUP -> new BackupFragment();
                    default ->
                            throw new IllegalStateException("Unexpected value: " + getIntent().getStringExtra(ARG_FRAGMENT));
                }
                : new AdminUsersFragment();
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }
}
