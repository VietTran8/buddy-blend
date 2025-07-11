package vn.tdtu.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceUtils {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ResourceUtils.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application.properties", ex);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
