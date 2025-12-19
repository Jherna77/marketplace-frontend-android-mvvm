package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.BackupDto;
import com.jhernandez.frontend.bazaar.domain.model.Backup;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Mapper class for converting between BackupDto and Backup domain model.
 */
public class BackupMapper {

    public static Backup toDomain(BackupDto backupDto) {
        return new Backup(backupDto.id(), backupDto.createdAt());
    }

    public static List<Backup> toDomainList(List<BackupDto> backupDtoList) {
        return backupDtoList.stream().map(BackupMapper::toDomain).collect(Collectors.toList());
    }


}
