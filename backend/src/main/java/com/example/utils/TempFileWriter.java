package com.example.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempFileWriter {

    public static File writeToFile(String code) throws Exception {
        // 1. Creates a unique temporary directory for THIS specific request (e.g., /tmp/java-analyzer-812371298)
        Path tempDir = Files.createTempDirectory("java-analyzer-");
        
        // 2. Creates the file inside that unique directory
        Path tempFile = tempDir.resolve("Main.java");
        
        // 3. Writes the code safely (Files.writeString handles opening/closing automatically)
        Files.writeString(tempFile, code);
        
        return tempFile.toFile();
    }
    
    
   // Helper method to clean up after analysis is done
    public static void deleteTempFileAndDir(File file) {
        if (file != null && file.exists()) {
            File parentDir = file.getParentFile();
            
            // Delete Main.java and log if it fails
            if (!file.delete()) {
                System.err.println("Failed to delete temp file: " + file.getAbsolutePath());
            }
            
            if (parentDir != null && parentDir.getName().startsWith("java-analyzer-")) {
                // Delete the unique temp folder and log if it fails
                if (!parentDir.delete()) {
                    System.err.println("Failed to delete temp directory: " + parentDir.getAbsolutePath());
                }
            }
        }
    }
}