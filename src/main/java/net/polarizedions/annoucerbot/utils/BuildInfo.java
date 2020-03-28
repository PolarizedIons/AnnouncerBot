package net.polarizedions.annoucerbot.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class BuildInfo {
    private static final Logger log = LogManager.getLogger(BuildInfo.class.getSimpleName());

    public static String version = "Unknown";
    public static String buildtime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());

    static {
        Properties buildInfo = new Properties();
        try {
            buildInfo.load(BuildInfo.class.getResourceAsStream("/buildinfo.txt"));
        }
        catch (IOException ex) {
            log.error("Error loading build information", ex);
        }

        Class<BuildInfo> clazz = BuildInfo.class;
        for (Field field : clazz.getFields()) {
            String value = buildInfo.getProperty(field.getName());
            if (value == null || (value.startsWith("${") && value.endsWith("}"))) {
                continue;
            }

            try {
                field.set(null, value);
            }
            catch (IllegalAccessException ex) {
                log.error("Error setting build info value", ex);
            }
        }
    }
}
