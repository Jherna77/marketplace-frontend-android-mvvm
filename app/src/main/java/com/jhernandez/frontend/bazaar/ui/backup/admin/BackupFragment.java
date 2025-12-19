package com.jhernandez.frontend.bazaar.ui.backup.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentBackupBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Backup;
import com.jhernandez.frontend.bazaar.ui.backup.detail.BackupDetailActivity;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ItemListCheckAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.util.ArrayList;

import lombok.NoArgsConstructor;


/*
 * Fragment representing the BackupFragment.
 * Displays a list of backups and handles user interactions related to backups.
 * Uses a ViewModel to manage the data and business logic.
 * Uses data binding to bind the UI components to the data.
 */
@NoArgsConstructor
public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;
    private BackupViewModel viewModel;
    private ItemListCheckAdapter<Backup> adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = getContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(BackupViewModel.class);
    }

    private void initAdapters() {
        adapter = new ItemListCheckAdapter<>(
                new ArrayList<>(),
                new ItemListCheckAdapter.ItemBinder<>() {
                    @Override
                    public String getItemTitle(Backup backup) {
                        return backup.id() + " - " +
                                backup.createdAt();
                    }

                    @Override
                    public void onCheckClicked(Backup backup) {
                        viewModel.onCheckBackupSelected(backup.id());
                    }
                }
        );
        binding.rvBackups.setAdapter(adapter);    }

    private void initListeners() {
        binding.addFab.setOnClickListener(v -> viewModel.onCreateBackupUserSelected());
    }

    private void initObservers() {
        viewModel.getBackups().observe(owner, adapter::updateItems);
        viewModel.goToBackupDetailEvent().observe(owner, this::goToBackupDetail);
        viewModel.isSuccess().observe(owner, this::onSuccess);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void goToBackupDetail(Long backupId) {
        startActivity(BackupDetailActivity.create(context, backupId));
    }

    private void onSuccess(Boolean isSuccess) {
        ViewUtils.showSuccessToast(context, isSuccess);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }
}