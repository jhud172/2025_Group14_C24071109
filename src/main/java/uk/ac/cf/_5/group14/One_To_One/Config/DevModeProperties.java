package uk.ac.cf._5.group14.One_To_One.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
public class DevModeProperties {
    private boolean devMode = false;
    private Map<String, String> envVariables = new HashMap<>();

    @PostConstruct
    public void loadEnvFile() {
        // Load .env file if it exists
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        envVariables.put(key, value);
                        // Also set as system property for consistency
                        System.setProperty(key, value);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading .env file: " + e.getMessage());
            }
        }
    }

    public boolean isDevMode() {
        // Check system environment variable first (highest priority - Set at runtime)
        String devModeEnv = System.getenv("DEV_MODE");
        if (devModeEnv != null) {
            return "true".equalsIgnoreCase(devModeEnv);
        }
        
        // Check system properties (from .env file)
        String devModeProp = System.getProperty("DEV_MODE");
        if (devModeProp != null) {
            return "true".equalsIgnoreCase(devModeProp);
        }
        
        // Check loaded env variables from .env file
        String devModeFromFile = envVariables.get("DEV_MODE");
        if (devModeFromFile != null) {
            return "true".equalsIgnoreCase(devModeFromFile);
        }
        
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }
}
