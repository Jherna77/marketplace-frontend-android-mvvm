package com.jhernandez.frontend.bazaar.ui.payment;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CART;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityPaymentBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;


/*
 * Activity that handles the payment process.
 * It initializes Stripe payment sheet, manages user interactions, and observes payment results.
 */
public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private PaymentViewModel viewModel;
    private PaymentSheet paymentSheet;

    public static Intent create(Context context) {
        return new Intent(context, PaymentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.order_info_title);
        initViewModel();
        initStripe();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(PaymentViewModel.class);
    }

    private void initStripe() {
        PaymentConfiguration.init(getApplicationContext(), PaymentViewModel.PUBLIC_KEY);
        paymentSheet = new PaymentSheet(this, viewModel::onPaymentSheetResult);
    }

    private void initListeners() {
        binding.btnMakePayment.setOnClickListener(v -> viewModel.onMakePaymentSelected(paymentSheet));
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
        viewModel.getProductsTotal().observe(this, this::setProductsTotal);
        viewModel.getShippingTotal().observe(this, this::setShippingTotal);
        viewModel.getOrderTotal().observe(this, this::setOrderTotal);
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.isCancelledAction().observe(this, this::onCancelledAction);
        viewModel.getApiError().observe(this, this::onApiError);
        viewModel.showPaymentErrorEvent().observe(this, this::onPaymentError);
    }

    private void setUserInfo(User user) {
        binding.tvName.setText(ViewUtils.nameFormatter(user));
        binding.tvAddress.setText(user.address());
        binding.tvCity.setText(ViewUtils.cityFormatter(user));
    }

    private void setProductsTotal(Double productsTotal) {
        binding.tvProducts.setText(String.format(
                getString(R.string.products_tv),
                productsTotal));
    }

    private void setShippingTotal(Double shippingTotal) {
        binding.tvShipping.setText(String.format(
                getString(R.string.shipping_tv),
                shippingTotal));
    }

    private void setOrderTotal(Double orderTotal) {
        binding.tvTotalAmount.setText(String.format(
                getString(R.string.total_tv),
                orderTotal));
    }

    private void onSuccess(Boolean isSuccess) {
        if (isSuccess) {
            ViewUtils.showToast(this, R.string.toast_success_action);
            startActivity(MainActivity.create(this));
            finish();
        }
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(this, cancelled);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void onPaymentError(Boolean show) {
        if (show) { ViewUtils.errorDialog(this, R.string.payment_error); }
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_CART));
        finish();
    }

}