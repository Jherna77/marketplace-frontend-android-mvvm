package com.jhernandez.frontend.bazaar.ui.user.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentAdminUsersBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListEditAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.user.manage.ManageUserActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

/*
 * Fragment for displaying and managing users in the admin section.
 * It handles displaying the list of users, filtering, and navigation to edit user details.
 */
@NoArgsConstructor
public class AdminUsersFragment extends Fragment {

    private FragmentAdminUsersBinding binding;
    private AdminUsersViewModel viewModel;
    private ItemListEditAdapter<User> adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(AdminUsersViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initAdapters() {
        adapter = new ItemListEditAdapter<>(
                new ArrayList<>(),
                new ItemListEditAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(User user) {
                        return user.name();
                    }

                    @Override
                    public void onEditClicked(User user) {
                        viewModel.onEditUserSelected(user.id());
                    }
                }
        );
        binding.rvUsers.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAllRoles.setOnClickListener(v -> viewModel.onRoleFilterSelected(binding.chipAllRoles.getTag().toString()));
        binding.chipCustomer.setOnClickListener(v -> viewModel.onRoleFilterSelected(binding.chipCustomer.getTag().toString()));
        binding.chipShop.setOnClickListener(v -> viewModel.onRoleFilterSelected(binding.chipShop.getTag().toString()));
        binding.chipAdmin.setOnClickListener(v -> viewModel.onRoleFilterSelected(binding.chipAdmin.getTag().toString()));
        binding.chipAllStatuses.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipAllStatuses.getTag().toString()));
        binding.chipEnabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipEnabled.getTag().toString()));
        binding.chipDisabled.setOnClickListener(v -> viewModel.onStatusFilterSelected(binding.chipDisabled.getTag().toString()));
        binding.chipAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipAscending.getTag().toString()));
        binding.chipDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipDescending.getTag().toString()));
        binding.addFab.setOnClickListener(v -> viewModel.onAddUserSelected());
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getFilteredUsers().observe(owner, adapter::updateItems);
        viewModel.goToAddUserEvent().observe(owner, go -> goToAddUser());
        viewModel.goToEditUserEvent().observe(owner, this::goToEditUser);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(AdminUsersViewState viewState) {
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToAddUser() {
        startActivity(ManageUserActivity.create(context));
        getActivity().finish();
    }

    private void goToEditUser(Long userId) {
        startActivity(ManageUserActivity.create(context, userId));
        getActivity().finish();
    }

}
