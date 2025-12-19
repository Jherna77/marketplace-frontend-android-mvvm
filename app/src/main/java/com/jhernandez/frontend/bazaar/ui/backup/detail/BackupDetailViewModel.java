package com.jhernandez.frontend.bazaar.ui.backup.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Backup;
import com.jhernandez.frontend.bazaar.domain.port.BackupRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for BackupDetailActivity.
 * Manages the data and business logic related to a specific backup.
 * Interacts with the BackupRepositoryPort to perform operations.
 */
public class BackupDetailViewModel extends BaseViewModel {

    private final BackupRepositoryPort backupRepository;
    private MutableLiveData<Backup> backup = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public BackupDetailViewModel(BackupRepositoryPort backupRepository) {
        this.backupRepository = backupRepository;
    }

    public LiveData<Backup> getBackup() {
        return backup;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void findBackupById(Long id) {
        Log.d("BackupDetailViewModel", "Finding backup with ID " + id);
        backupRepository.findBackupById(id, typeCallback(backup));
    }

    private void restoreBackup() {
        Log.d("BackupDetailViewModel", "Restoring backup...");
        backupRepository.restoreBackup(backup.getValue().id(), successCallback());
    }

    public void onRestoreBackupSelected() {
        restoreBackup();
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
