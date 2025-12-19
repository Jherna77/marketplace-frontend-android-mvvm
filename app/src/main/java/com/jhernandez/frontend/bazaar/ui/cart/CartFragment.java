package com.jhernandez.frontend.bazaar.ui.cart;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_HOME;
import static com.jhernandez.frontend.bazaar.ui.cart.CartViewModel.MAX_QUANTITY;
import static com.jhernandez.frontend.bazaar.ui.cart.CartViewModel.MIN_QUANTITY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentCartBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;
import com.jhernandez.frontend.bazaar.ui.order.adapter.CartItemAdapter;
import com.jhernandez.frontend.bazaar.ui.payment.PaymentActivity;
import com.jhernandez.frontend.bazaar.ui.product.detail.ProductDetailActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment representing the CartFragment.
 * Displays the user's shopping cart and handles user interactions related to it.
 * Uses a ViewModel to manage the data and business logic.
 * Uses data binding to bind the UI components to the data.
 */
@NoArgsConstructor
public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartViewModel viewModel;
    private CartItemAdapter adapter;
    private Context context;
    private LifecycleOwner owner;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initProgressDialog();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(CartViewModel.class);
    }

    private void initViewState() {
        viewModel.setViewState();
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(context);
    }

    private void initAdapters() {
        adapter = new CartItemAdapter(new ArrayList<>(), new CartItemAdapter.OnProductActionListener() {
            @Override
            public void onItemClicked(Long productId) {
                viewModel.onItemSelected(productId);
            }

            @Override
            public void onQuantityClicked(Long itemId) {
                viewModel.onItemQuantitySelected(itemId);
            }

            @Override
            public void onDeleteClicked(Long itemId) {
                viewModel.onRemoveItemSelected(itemId);
            }
        });
        binding.rvCartItems.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnKeepBuying.setOnClickListener(v -> viewModel.onKeepBuyingSelected());
        binding.btnProcessOrder.setOnClickListener(v -> viewModel.onProcessOrderSelected());
        binding.btnClearCart.setOnClickListener(v -> viewModel.onClearCartSelected());
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getItems().observe(owner, adapter::updateItems);
        viewModel.getPriceTotal().observe(owner, this::updatePriceTotal);
        viewModel.getShippingTotal().observe(owner, this::updateShippingTotal);
        viewModel.showItemQuantityDialogEvent().observe(owner, this::showItemQuantityDialog);
        viewModel.clearCartEvent().observe(owner, clear -> clearCartConfirmation());
        viewModel.isCancelledAction().observe(owner, this::onCancelledAction);
        viewModel.goToHomeEvent().observe(owner, go -> goToHome());
        viewModel.goToProductDetailEvent().observe(owner, this::goToProductDetail);
        viewModel.goToPaymentEvent().observe(owner, go -> goToPayment());
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(CartViewState state) {
        if (state.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
        binding.rvCartItems.setVisibility(state.isEmpty() ? View.GONE : View.VISIBLE);
        binding.llCartAmount.setVisibility(state.isEmpty() ? View.GONE : View.VISIBLE);
        binding.priceInfo.setVisibility(state.isEmpty() ? View.GONE : View.VISIBLE);
        binding.btnProcessOrder.setVisibility(state.isEmpty() ? View.GONE : View.VISIBLE);
        binding.btnClearCart.setVisibility(state.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvInfo.setVisibility(state.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updatePriceTotal(Double priceTotal) {
        binding.subtotalAmount.setText(String.format(
                getString(R.string.tv_subtotal_amount),
                priceTotal));
    }

    private void updateShippingTotal(Double shippingTotal) {
        binding.shippingAmount.setText(String.format(
                getString(R.string.tv_shipping),
                shippingTotal));
    }

    private void showItemQuantityDialog(Long itemId) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quantity_picker, null);
        NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMinValue(MIN_QUANTITY);
        numberPicker.setMaxValue(MAX_QUANTITY);
        numberPicker.setWrapSelectorWheel(true);

        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.select_quantity)
                .setView(dialogView)
                .setPositiveButton(
                        R.string.ok_resp,
                        (dialog, which) ->
                                viewModel.onItemQuantityChanged(
                                        itemId,
                                        numberPicker.getValue()))
                .show();
    }

    private void clearCartConfirmation() {
        ViewUtils.confirmActionDialog(
                context,
                getString(R.string.clear_cart_warn),
                getString(R.string.clear_cart_msg),
                viewModel::onClearCartConfirmation);
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(context, cancelled);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorToast(context, error);
    }

    private void goToHome() {
        startActivity(MainActivity.create(context, ARG_HOME));
    }

    private void goToProductDetail(Long productId) {
        startActivity(ProductDetailActivity.create(context, productId));
    }

    private void goToPayment() {
        startActivity(PaymentActivity.create(requireContext()));
        requireActivity().finish();
    }

}
