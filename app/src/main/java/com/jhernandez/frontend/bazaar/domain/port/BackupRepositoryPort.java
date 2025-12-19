package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Backup;

import java.util.List;


/*
 * Interface representing the BackupRepositoryPort.
 */
public interface BackupRepositoryPort {

    void createBackup(SuccessCallback callback);
    void findAllBackups(TypeCallback<List<Backup>> callback);
    void findBackupById(Long id, TypeCallback<Backup> callback);
    void restoreBackup(Long id, SuccessCallback callback);

}
