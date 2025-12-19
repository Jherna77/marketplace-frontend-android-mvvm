package com.jhernandez.frontend.bazaar.ui.order.purchase.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_ORDER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityPurchaseOrderDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Item;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.util.OrderStatusUtils;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.order.purchase.customer.PurchaseOrdersActivity;
import com.jhernandez.frontend.bazaar.ui.review.manage.ManageReviewActivity;

/*
 * Activity that displays the details of a specific purchase order.
 * It allows the user to view order information, cancel the order, and leave a review.
 */
public class PurchaseOrderDetailActivity extends AppCompatActivity {

    private ActivityPurchaseOrderDetailBinding binding;
    private PurchaseOrderDetailViewModel viewModel;
    private Long orderId;
    private AlertDialog progressDialog;

    public static Intent create(Context context, Long id) {
        return new Intent(context, PurchaseOrderDetailActivity.class).putExtra(ARG_ORDER, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseOrderDetailBinding.inflate(getLayoutInflater());
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
                .get(PurchaseOrderDetailViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState(orderId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.tvLeaveReview.setOnClickListener(v -> viewModel.onLeaveReviewSelected());
        binding.btnCancelOrder.setOnClickListener(v -> cancelOrderConfirmation());
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
        viewModel.getShop().observe(this, this::setShopInfo);
        viewModel.getItem().observe(this, this::setItemInfo);
        viewModel.isCancelledAction().observe(this, show -> ViewUtils.showCancelToast(this, show));
        viewModel.goToLeaveReviewEvent().observe(this, go -> goToLeaveReview());
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(PurchaseOrderDetailViewState state) {
        binding.header.title.setText(R.string.order_detail);
        if (state.isLoading()) { progressDialog.show(); } else { progressDialog.dismiss(); }
    }

    private void setOrderInfo(Order order) {
        binding.tvOrderNumber.setText(String.format(getString(R.string.order_number_tv), order.id()));
        binding.tvOrderDate.setText(String.format(getString(R.string.order_date_tv), order.orderDate()));
        binding.tvOrderAmount.setText(String.format(getString(R.string.amount_tv), order.item().getTotalPrice()));
        binding.tvOrderStatus.setText(
                String.format(
                        getString(R.string.order_status_tv),
                        OrderStatusUtils.getLabel(this, order.status())));    }

    private void setShopInfo(User shop) {
        binding.tvOrderShop.setText(String.format(getString(R.string.shop_tv), ViewUtils.nameEmailFormatter(shop)));
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

    private void cancelOrderConfirmation() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.cancel_order_warn),
                getString(R.string.cancel_order_msg),
                viewModel::onCancelOrderConfirmation);
    }

    private void goToLeaveReview() {
        startActivity(ManageReviewActivity.create(this, orderId));
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void goBack() {
        startActivity(PurchaseOrdersActivity.create(this));
        finish();
    }

}