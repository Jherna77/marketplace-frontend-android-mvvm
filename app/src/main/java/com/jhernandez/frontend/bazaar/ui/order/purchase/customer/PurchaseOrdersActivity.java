package com.jhernandez.frontend.bazaar.ui.order.purchase.customer;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityPurchaseOrdersBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListCheckAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.order.purchase.detail.PurchaseOrderDetailActivity;

import java.util.ArrayList;

/*
 * Activity that displays a list of purchase orders for the customer.
 * It allows filtering and sorting of orders, and navigation to order details.
 */
public class PurchaseOrdersActivity extends AppCompatActivity {

    private ActivityPurchaseOrdersBinding binding;
    private PurchaseOrdersViewModel viewModel;
    private ItemListCheckAdapter<Order> adapter;

    public static Intent create(Context context) {
        return new Intent(context, PurchaseOrdersActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
    }

    private void initUI() {
        initViewModel();
        initViewState();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(PurchaseOrdersViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initAdapters() {
        adapter = new ItemListCheckAdapter<>(
                new ArrayList<>(),
                new ItemListCheckAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Order order) {
                        return ViewUtils.orderFormatter(PurchaseOrdersActivity.this, order);
                    }

                    @Override
                    public void onCheckClicked(Order order) {
                        viewModel.onCheckOrderSelected(order.id());
                    }
                }
        );
        binding.rvOrders.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAllStatuses.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipAllStatuses.getTag().toString()));
        binding.chipPending.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipPending.getTag().toString()));
        binding.chipConfirmed.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipConfirmed.getTag().toString()));
        binding.chipShipped.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipShipped.getTag().toString()));
        binding.chipDelivered.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipDelivered.getTag().toString()));
        binding.chipCancelled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipCancelled.getTag().toString()));
        binding.chipReturned.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipReturned.getTag().toString()));
        binding.chipAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipAscending.getTag().toString()));
        binding.chipDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipDescending.getTag().toString()));
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
        viewModel.getFilteredOrders().observe(this, adapter::updateItems);
        viewModel.goToOrderDetailEvent().observe(this, this::goToOrderDetail);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(PurchaseOrdersViewState viewState) {
        binding.header.title.setText(R.string.orders);
        binding.btnFilter.setEnabled(viewState.hasResults());
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
        binding.tvInfo.setVisibility(!viewState.hasResults() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goToOrderDetail(Long orderId) {
        startActivity(PurchaseOrderDetailActivity.create(this, orderId));
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }
}