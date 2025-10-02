package api.configs;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
@Slf4j
public class Config {
    private final static Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Fail to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        log.info(key);
        String systemValue = System.getProperty(key);
        log.info(systemValue);
        if (systemValue != null) {
            return systemValue;
        }
        String systemEnv = System.getenv(key.toUpperCase().replace(".", "_"));
        log.info(systemEnv);
        if (systemEnv != null) {
            return systemEnv;
        }
        String propertiesValue = INSTANCE.properties.getProperty(key);
        log.info(propertiesValue);
        return propertiesValue;
    }
}
