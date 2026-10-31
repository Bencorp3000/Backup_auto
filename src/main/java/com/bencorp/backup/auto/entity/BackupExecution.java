package com.bencorp.backup.auto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_execution")
public class BackupExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackupStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime finishedAt;

    @Column
    private Long durationMs;

    @Column
    private Long fileSizeBytes;

    @Column(nullable = false)
    private Boolean launchedManually;

    @Column(length = 2000)
    private String failureReason;

    public BackupExecution() {
    }

    public BackupExecution(BackupStatus status, LocalDateTime startedAt, LocalDateTime finishedAt,
                          Long durationMs, Long fileSizeBytes, Boolean launchedManually,
                          String failureReason) {
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.durationMs = durationMs;
        this.fileSizeBytes = fileSizeBytes;
        this.launchedManually = launchedManually;
        this.failureReason = failureReason;
    }

    public Long getId() {
        return id;
    }

    public BackupStatus getStatus() {
        return status;
    }

    public void setStatus(BackupStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Boolean getLaunchedManually() {
        return launchedManually;
    }

    public void setLaunchedManually(Boolean launchedManually) {
        this.launchedManually = launchedManually;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
