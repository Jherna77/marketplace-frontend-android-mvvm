package com.jhernandez.frontend.bazaar.ui.backup.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Backup;
import com.jhernandez.frontend.bazaar.domain.port.BackupRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.List;

/*
 * ViewModel for BackupFragment.
 * Manages the data and business logic related to backups.
 * Interacts with the BackupRepositoryPort to perform operations.
 */
public class BackupViewModel extends BaseViewModel {

    private final BackupRepositoryPort backupRepository;
    private final MutableLiveData<List<Backup>> backups = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToBackupDetail = new MutableLiveData<>();

    public BackupViewModel(BackupRepositoryPort backupRepository) {
        this.backupRepository = backupRepository;
        findAllBackups();
    }

    public LiveData<List<Backup>> getBackups() {
        return backups;
    }
    public LiveData<Long> goToBackupDetailEvent() {
        return _goToBackupDetail;
    }

    private void createBackup() {
        Log.d("BackupViewModel", "Creating backup...");
        backupRepository.createBackup(successCallback(
                this::findAllBackups,
                apiError ->{}
        ));
    }

    private void findAllBackups() {
        Log.d("BackupViewModel", "Loading all backups...");
        backupRepository.findAllBackups(typeCallback(backups));
    }

    public void onCheckBackupSelected(Long backupId) {
        _goToBackupDetail.setValue(backupId);
    }

    public void onCreateBackupUserSelected() {
        createBackup();
    }


}
