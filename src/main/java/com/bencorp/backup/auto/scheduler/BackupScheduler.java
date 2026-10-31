package com.bencorp.backup.auto.scheduler;

import com.bencorp.backup.auto.service.BackupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    private final BackupService backupService;

    public BackupScheduler(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "${backup.scheduler.cron}")
    public void scheduledBackup() {
        backupService.triggerAutomaticBackup();
    }
}
