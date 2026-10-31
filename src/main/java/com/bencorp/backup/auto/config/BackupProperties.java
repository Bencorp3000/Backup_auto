package com.bencorp.backup.auto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backup")
public class BackupProperties {

    private Source source = new Source();
    private Drive drive = new Drive();
    private Scheduler scheduler = new Scheduler();

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Drive getDrive() {
        return drive;
    }

    public void setDrive(Drive drive) {
        this.drive = drive;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static class Source {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Drive {
        private String folderId;
        private String credentialsPath;
        private String applicationName = "Backup Auto";

        public String getFolderId() {
            return folderId;
        }

        public void setFolderId(String folderId) {
            this.folderId = folderId;
        }

        public String getCredentialsPath() {
            return credentialsPath;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public void setCredentialsPath(String credentialsPath) {
            this.credentialsPath = credentialsPath;
        }
    }

    public static class Scheduler {
        private String cron = "0 0 2 15 * *";

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}
