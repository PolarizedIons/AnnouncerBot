package net.polarizedions.annoucerbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;

public class BotConfig {
    private static final Logger log = LogManager.getLogger(BotConfig.class.getSimpleName());
    private static BotConfig instance;

    public String discordToken;

    public static BotConfig getInstance() {
        if (instance == null) {
            File configFile = Path.of("config", "config.json").toFile();
            log.info("Loading config from {}", configFile);
            try {
                instance = Constants.GSON.fromJson(new FileReader(configFile), BotConfig.class);
            } catch (FileNotFoundException e) {
                try {
                    log.info("Creating new config, please fill it out!");
                    FileWriter fw = new FileWriter(configFile);
                    fw.write(Constants.GSON.toJson(new BotConfig()));
                    fw.close();
                } catch (IOException ex) {
                    log.error("Error creating new config!");
                }
                System.exit(0);
            }
        }

        return instance;
    }
}
