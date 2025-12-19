package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.BackupMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Backup;
import com.jhernandez.frontend.bazaar.domain.port.BackupRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing backup-related operations.
 */
@RequiredArgsConstructor
public class BackupRepository implements BackupRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createBackup(SuccessCallback callback) {
        apiService.createBackup()
                .enqueue(CallbackDelegator.delegate("createBackup", callback));
    }

    @Override
    public void findAllBackups(TypeCallback<List<Backup>> callback) {
        apiService.findAllBackups()
                .enqueue(CallbackDelegator.delegate(
                        "findAllBackups",
                        response ->
                                callback.onSuccess(BackupMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findBackupById(Long id, TypeCallback<Backup> callback) {
        apiService.findBackupById(id)
                .enqueue(CallbackDelegator.delegate(
                        "findBackupById",
                        response ->
                                callback.onSuccess(BackupMapper.toDomain(response)),
                                callback::onError));
    }

    @Override
    public void restoreBackup(Long id, SuccessCallback callback) {
        apiService.restoreBackup(id)
                .enqueue(CallbackDelegator.delegate("restoreBackup", callback));
    }
}
