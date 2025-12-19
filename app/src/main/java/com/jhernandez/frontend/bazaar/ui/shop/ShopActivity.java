package com.jhernandez.frontend.bazaar.ui.shop;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_FRAGMENT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_ORDER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.databinding.ActivityShopBinding;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.order.sale.shop.SaleOrdersFragment;
import com.jhernandez.frontend.bazaar.ui.product.shop.ShopProductsFragment;


/*
 * Activity that manages the shop interface.
 * It handles navigation between product and order fragments within the shop.
 */
public class ShopActivity extends AppCompatActivity {

    private ActivityShopBinding binding;
    private Integer containerViewId;

    public static Intent create(Context context) {
        return new Intent(context, ShopActivity.class);
    }

    public static Intent create(Context context, String fragment) {
        return new Intent(context, ShopActivity.class).putExtra(ARG_FRAGMENT, fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        initHeader();
        initBottomNavMenu();
        initListeners();
    }

    private void initHeader() {
        binding.header.title.setText(R.string.shop);
    }

    private void initBottomNavMenu() {
        containerViewId = R.id.fragment_container_shop;
        replaceFragment(containerViewId, getDefaultFragment());
    }

    private void initListeners() {
        binding.bottomNavigationShop.setOnItemSelectedListener(item -> {
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
        if (itemId == R.id.nav_products) replaceFragment(containerViewId, new ShopProductsFragment());
        else if (itemId == R.id.nav_orders) replaceFragment(containerViewId, new SaleOrdersFragment());
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
            case ARG_PRODUCT -> new ShopProductsFragment();
            case ARG_ORDER -> new SaleOrdersFragment();
            default ->
                    throw new IllegalStateException("Unexpected value: " + getIntent().getStringExtra(ARG_FRAGMENT));
        }
                : new ShopProductsFragment();
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }
}