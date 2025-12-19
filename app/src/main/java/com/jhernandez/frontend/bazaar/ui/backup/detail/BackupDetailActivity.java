package com.jhernandez.frontend.bazaar.ui.backup.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_BACKUP;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityBackupDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Backup;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

/*
 * Activity representing the BackupDetailActivity.
 * Displays the details of a specific backup and handles user interactions related to it.
 * Uses a ViewModel to manage the data and business logic.
 * Uses data binding to bind the UI components to the data.
 */
public class BackupDetailActivity extends AppCompatActivity {

    private ActivityBackupDetailBinding binding;
    private BackupDetailViewModel viewModel;
    private Long backupId;

    public static Intent create(Context context, Long id) {
        return new Intent(context, BackupDetailActivity.class).putExtra(ARG_BACKUP, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBackupDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        backupId = getIntent().getLongExtra(ARG_BACKUP, NO_ARG);
        initUI();
    }

    private void initUI() {
        binding.header.title.setText(R.string.backup_detail);
        initViewModel();
        initListeners();
        initObservers();
        viewModel.findBackupById(backupId);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(BackupDetailViewModel.class);
    }

    private void initListeners() {
        binding.btnRestoreBackup.setOnClickListener(v -> viewModel.onRestoreBackupSelected());
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getBackup().observe(this, this::setBackupInfo);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void setBackupInfo(Backup backup) {
        binding.tvBackupNumber.setText(String.format(
                getString(R.string.backup_number_tv),
                backup.id()));
        binding.tvBackupDate.setText(String.format(
                getString(R.string.backup_date_tv),
                backup.createdAt()));
    }

    private void onSuccess(Boolean isSuccess) {
        ViewUtils.showSuccessToast(this, isSuccess);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goBack() {
        finish();
    }

}