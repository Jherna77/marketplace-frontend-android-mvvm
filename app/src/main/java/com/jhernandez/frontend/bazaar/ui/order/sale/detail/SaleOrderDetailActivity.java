package com.jhernandez.frontend.bazaar.ui.order.sale.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_ORDER;
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
import com.jhernandez.frontend.bazaar.databinding.ActivitySaleOrderDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.util.OrderStatusUtils;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.shop.ShopActivity;

import java.util.Arrays;

/*
 * Activity that displays the details of a specific sale order.
 * It allows the user to view order information, update the order status, and navigate back.
 */
public class SaleOrderDetailActivity extends AppCompatActivity {

    private ActivitySaleOrderDetailBinding binding;
    private SaleOrderDetailViewModel viewModel;
    private Long orderId;
    private AlertDialog progressDialog;

    public static Intent create(Context context, Long id) {
        return new Intent(context, SaleOrderDetailActivity.class).putExtra(ARG_ORDER, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaleOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        orderId = getIntent().getLongExtra(ARG_ORDER, NO_ARG);
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
                .get(SaleOrderDetailViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState(orderId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.setOrderStatus.setOnClickListener(v -> viewModel.onSelectStatusClicked());
        binding.btnUpdateStatus.setOnClickListener(v -> viewModel.onUpdateStatusSelected());
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
        viewModel.getOrder().observe(this, this::setOrderInfo);
        viewModel.getCustomer().observe(this, this::setCustomerInfo);
        viewModel.getItem().observe(this, this::setItemInfo);
        viewModel.showOrderStatusDialogEvent().observe(this, this::showOrderStatusDialog);
        viewModel.getOrderStatus().observe(this, this::updateSetOrderStatus);
        viewModel.confirmStatusUpdateEvent().observe(this, this::updateStatusConfirmation);
        viewModel.isCancelledAction().observe(this, show -> ViewUtils.showCancelToast(this, show));
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(SaleOrderDetailViewState state) {
        binding.header.title.setText(R.string.sale_detail);
        if (state.isLoading()) { progressDialog.show(); } else { progressDialog.dismiss(); }
        binding.tvErrors.setVisibility(state.hasErrors() ? View.VISIBLE : View.GONE);
    }

    private void setOrderInfo(Order order) {
        binding.tvOrderNumber.setText(String.format(getString(R.string.order_number_tv), order.id()));
        binding.tvOrderDate.setText(String.format(getString(R.string.order_date_tv), order.orderDate()));
        binding.tvOrderAmount.setText(String.format(getString(R.string.amount_tv), order.item().getTotalPrice()));
        binding.tvOrderStatus.setText(
                String.format(
                        getString(R.string.order_status_tv),
                        OrderStatusUtils.getLabel(this, order.status())));
    }

    private void setCustomerInfo(User customer) {
        binding.tvOrderCustomer.setText(String.format(getString(R.string.customer_tv), customer.name()));
        binding.tvCustomerEmail.setText(String.format(getString(R.string.email_tv), customer.email()));
        binding.tvCustomerAddress.setText(String.format(getString(R.string.sale_address_tv), ViewUtils.addressFormatter(customer)));
    }

    private void setItemInfo(Item item) {
        binding.tvProductName.setText(item.getProduct().name());
        binding.tvProductPrice.setText(String.format(
                getString(R.string.price_format),
                item.getSalePrice()));
        binding.tvProductShipping.setText(String.format(
                getString(R.string.shipping_tv),
                item.getSaleShipping()));
        binding.tvQuantity.setText(String.format(
                getString(R.string.quantity_of_products),
                item.getQuantity()));
        ViewUtils.showImageOnImageView(this, item.getProduct().imagesUrl().get(0), binding.productImage);
    }

    private void updateSetOrderStatus(String status) {
        binding.setOrderStatus.setText(OrderStatusUtils.getLabel(this, status));
    }

    private void showOrderStatusDialog(String[] allOrderStatuses) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.order_change_status)
                .setItems(
                        Arrays.stream(allOrderStatuses)
                                .map(status -> OrderStatusUtils.getLabel(this, status))
                                .toArray(String[]::new),
                        (dialog, which) ->
                                viewModel.onOrderStatusSelected(allOrderStatuses[which])
                )
                .show();
    }

    private void updateStatusConfirmation(String status) {
        ViewUtils.confirmActionDialog(
                    this,
                    getString(R.string.update_status_warn),
                    String.format(getString(R.string.update_status_msg),
                            OrderStatusUtils.getLabel(this,status)),
                    viewModel::onChangeStatusConfirmation);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void goBack() {
        startActivity(ShopActivity.create(this, ARG_ORDER));
        finish();
    }

}