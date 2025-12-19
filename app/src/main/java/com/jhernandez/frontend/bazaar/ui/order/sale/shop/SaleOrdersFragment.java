package com.jhernandez.frontend.bazaar.ui.order.sale.shop;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentSaleOrdersBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListCheckAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.order.sale.detail.SaleOrderDetailActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment that displays a list of sale orders for the shop.
 * It allows filtering and sorting of orders, and navigation to order details.
 */
@NoArgsConstructor
public class SaleOrdersFragment extends Fragment {

    private FragmentSaleOrdersBinding binding;
    private SaleOrdersViewModel viewModel;
    private ItemListCheckAdapter<Order> adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSaleOrdersBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(SaleOrdersViewModel.class);    }

    private void initAdapters() {
        adapter = new ItemListCheckAdapter<>(
                new ArrayList<>(),
                new ItemListCheckAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Order order) {
                        return ViewUtils.orderFormatter(context, order);
                    }

                    @Override
                    public void onCheckClicked(Order order) {
                        viewModel.onCheckOrderSelected(order.id());
                    }
                }
        );
        binding.rvSales.setAdapter(adapter);
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
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getFilteredOrders().observe(owner, adapter::updateItems);
        viewModel.goToOrderDetailEvent().observe(owner, this::goToOrderDetail);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(SaleOrdersViewState viewState) {
        binding.btnFilter.setEnabled(viewState.hasResults());
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
        binding.tvInfo.setVisibility(!viewState.hasResults() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToOrderDetail(Long orderId) {
        startActivity(SaleOrderDetailActivity.create(context, orderId));
    }



}