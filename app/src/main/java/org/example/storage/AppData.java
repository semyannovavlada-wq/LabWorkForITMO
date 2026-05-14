package org.example.storage;

import java.nio.file.Files;
import java.nio.file.Path;

public class AppData {

    private static Path folder;

    public static Path getFolder() {
        if (folder == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            String userHome = System.getProperty("user.home");
            String path;

            if (osName.contains("win")) {
                path = userHome + "\\AppData\\Roaming\\WaterLab";
            } else if (osName.contains("mac")) {
                path = userHome + "/Library/Application Support/WaterLab";
            } else {
                path = userHome + "/.WaterLab";
            }

            folder = Path.of(path);
            try {
                Files.createDirectories(folder);
            } catch (Exception e) {
                folder = Path.of(".");
            }
        }
        return folder;
    }

    public static Path resolve(String fileName) {
        return getFolder().resolve(fileName);
    }
}