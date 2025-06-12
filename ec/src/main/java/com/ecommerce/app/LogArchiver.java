package com.ecommerce.app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class LogArchiver {

    private static final String LOG_DIR = "logs";
    private static final String ARCHIVE_DIR = "logs/archived";

    @Scheduled(cron = "0 5 0 * * ?") // Every day at 00:05 AM
    public void archiveYesterdayLogs() {
        try {
            String yesterday = new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

            File logFolder = new File(LOG_DIR);
            if (!logFolder.exists()) return;

            File[] logFiles = logFolder.listFiles((dir, name) -> name.startsWith("app-" + yesterday) && name.endsWith(".log"));
            if (logFiles == null || logFiles.length == 0) return;

            File archiveFolder = new File(ARCHIVE_DIR);
            if (!archiveFolder.exists()) archiveFolder.mkdirs();

            File zipFile = new File(ARCHIVE_DIR + "/app-" + yesterday + ".zip");
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for (File file : logFiles) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry entry = new ZipEntry(file.getName());
                        zos.putNextEntry(entry);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                    file.delete(); // delete original .log
                }
            }

            System.out.println("Archived logs for: " + yesterday);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
