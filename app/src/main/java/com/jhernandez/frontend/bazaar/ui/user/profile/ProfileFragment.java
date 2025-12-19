package com.jhernandez.frontend.bazaar.ui.user.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentProfileBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.admin.AdminActivity;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.order.purchase.customer.PurchaseOrdersActivity;
import com.jhernandez.frontend.bazaar.ui.product.favourite.FavouriteProductsActivity;
import com.jhernandez.frontend.bazaar.ui.review.user.UserReviewsActivity;
import com.jhernandez.frontend.bazaar.ui.shop.ShopActivity;
import com.jhernandez.frontend.bazaar.ui.user.account.AccountActivity;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for displaying and managing the user's profile.
 * It handles navigation to account details, orders, favourites, reviews, shop, and admin sections.
 */
@NoArgsConstructor
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(ProfileViewModel.class);
    }

    private void initListeners() {
        binding.btnAccount.setOnClickListener(v -> viewModel.onAccountSelected());
        binding.btnOrders.setOnClickListener(v -> viewModel.onOrdersSelected());
        binding.btnFavourites.setOnClickListener(v -> viewModel.onFavouritesSelected());
        binding.btnReviews.setOnClickListener(v -> viewModel.onReviewsSelected());
        binding.btnShop.setOnClickListener(v -> viewModel.onShopSelected());
        binding.btnAdmin.setOnClickListener(v -> viewModel.onAdminSelected());
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getUser().observe(owner, this::setUserInfo);
        viewModel.goToAccountEvent().observe(owner, go -> goToAccount());
        viewModel.goToOrdersEvent().observe(owner, go -> goToOrders());
        viewModel.goToFavouritesEvent().observe(owner, go -> goToFavourites());
        viewModel.goToReviewsEvent().observe(owner, go -> goToReviews());
        viewModel.goToShopEvent().observe(owner, go -> goToShop());
        viewModel.goToAdminEvent().observe(owner, go -> goToAdmin());
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(ProfileViewState viewState) {
        binding.btnShop.setVisibility(viewState.isShop() || viewState.isAdmin() ? View.VISIBLE : View.GONE);
        binding.btnAdmin.setVisibility(viewState.isAdmin() ? View.VISIBLE : View.GONE);
    }

    private void setUserInfo(User user) {
        binding.tvWelcome.setText(String.format(
                getString(R.string.welcome),
                ViewUtils.nameFormatter(user)));
    }

    private void goToAccount() {
        startActivity(AccountActivity.create(context));
        requireActivity().finish();
    }

    private void goToOrders() {
        startActivity(PurchaseOrdersActivity.create(context));
        requireActivity().finish();
    }

    private void goToFavourites() {
        startActivity(FavouriteProductsActivity.create(context));
        requireActivity().finish();
    }

    private void goToReviews() {
        startActivity(UserReviewsActivity.create(context));
        requireActivity().finish();
    }

    private void goToShop() {
        startActivity(ShopActivity.create(context));
        requireActivity().finish();
    }

    private void goToAdmin() {
        startActivity(AdminActivity.create(context));
        requireActivity().finish();
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

}