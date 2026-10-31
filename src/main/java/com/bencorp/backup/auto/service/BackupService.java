package com.bencorp.backup.auto.service;

import com.bencorp.backup.auto.config.BackupProperties;
import com.bencorp.backup.auto.entity.BackupExecution;
import com.bencorp.backup.auto.entity.BackupStatus;
import com.bencorp.backup.auto.repository.BackupExecutionRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    private final BackupExecutionRepository repository;
    private final BackupProperties properties;

    public BackupService(BackupExecutionRepository repository, BackupProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public BackupExecution triggerManualBackup() {
        return executeBackup(true);
    }

    public BackupExecution triggerAutomaticBackup() {
        return executeBackup(false);
    }

    @Transactional
    public BackupExecution executeBackup(boolean manual) {
        LocalDateTime startedAt = LocalDateTime.now();
        BackupExecution execution = new BackupExecution();
        execution.setStartedAt(startedAt);
        execution.setLaunchedManually(manual);
        execution.setStatus(BackupStatus.FAILURE);

        try {
            Path sourcePath = Path.of(Objects.requireNonNull(properties.getSource().getPath(), "backup.source.path is required"));
            if (!Files.exists(sourcePath)) {
                throw new IllegalStateException("Source path does not exist: " + sourcePath);
            }

            Path zipFile = createZipArchive(sourcePath);
            uploadToGoogleDrive(zipFile);

            execution.setStatus(BackupStatus.SUCCESS);
            execution.setFileSizeBytes(Files.size(zipFile));
            execution.setFinishedAt(LocalDateTime.now());
            execution.setDurationMs(Duration.between(startedAt, execution.getFinishedAt()).toMillis());
            execution.setFailureReason(null);
            return repository.save(execution);
        } catch (Exception ex) {
            LocalDateTime finishedAt = LocalDateTime.now();
            execution.setFinishedAt(finishedAt);
            execution.setDurationMs(Duration.between(startedAt, finishedAt).toMillis());
            execution.setFailureReason(ex.getMessage());
            log.error("Backup execution failed", ex);
            return repository.save(execution);
        }
    }

    private Path createZipArchive(Path sourcePath) throws IOException {
        Path root = Files.createTempDirectory("backup-auto-");
        String zipName = "backup-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".zip";
        Path zipFile = root.resolve(zipName);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            zipDirectory(sourcePath, sourcePath.getFileName().toString(), zipOutputStream);
        }

        return zipFile;
    }

    private void zipDirectory(Path sourcePath, String relativeRoot, ZipOutputStream zipOutputStream) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            List<Path> paths = Files.list(sourcePath).sorted().toList();
            for (Path child : paths) {
                zipDirectory(child, relativeRoot + "/" + child.getFileName(), zipOutputStream);
            }
            return;
        }

        String entryName = relativeRoot;
        zipOutputStream.putNextEntry(new ZipEntry(entryName));
        Files.copy(sourcePath, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    private void uploadToGoogleDrive(Path zipFile) throws Exception {
        String credentialsPath = properties.getDrive().getCredentialsPath();
        String folderId = properties.getDrive().getFolderId();

        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException("backup.drive.credentials-path is required");
        }
        if (folderId == null || folderId.isBlank()) {
            throw new IllegalStateException("backup.drive.folder-id is required");
        }

        Drive service = createDriveService();
        List<com.google.api.services.drive.model.File> files = service.files().list()
            .setQ("'" + folderId + "' in parents and trashed = false")
            .setFields("files(id, name)")
            .execute()
            .getFiles();

        for (com.google.api.services.drive.model.File existingFile : files) {
            service.files().delete(existingFile.getId()).execute();
        }

        com.google.api.services.drive.model.File metadata = new File();
        metadata.setName(zipFile.getFileName().toString());
        metadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("application/zip", zipFile.toFile());
        service.files().create(metadata, mediaContent).execute();
    }

    private Drive createDriveService() throws Exception {
        com.google.api.client.googleapis.auth.oauth2.GoogleCredential credential = com.google.api.client.googleapis.auth.oauth2.GoogleCredential
            .fromStream(new FileInputStream(properties.getDrive().getCredentialsPath()))
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive"));

        return new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential)
            .setApplicationName(properties.getDrive().getApplicationName())
            .build();
    }
}
