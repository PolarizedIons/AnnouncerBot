package net.polarizedions.annoucerbot.bot;

import net.polarizedions.annoucerbot.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;

public class BotConfig {
    private static final Logger log = LogManager.getLogger(BotConfig.class.getSimpleName());
    private static final File CONFIG_FOLDER = new File("config");
    private static final File CONFIG_FILE = new File(Path.of(CONFIG_FOLDER.toString(), "config.json").toString());

    private static BotConfig instance;

    public String discordToken = "";
    public String prefix = "!";

    public static BotConfig getInstance() {
        if (instance == null) {
            log.info("Loading config from {}", CONFIG_FILE);
            try {
                instance = Main.Constants.GSON.fromJson(new FileReader(CONFIG_FILE), BotConfig.class);
            } catch (FileNotFoundException e) {
                try {
                    log.info("Creating new config, please fill it out!");

                    if (!CONFIG_FOLDER.exists()) {
                        CONFIG_FOLDER.mkdir();
                    }

                    FileWriter fw = new FileWriter(CONFIG_FILE);
                    fw.write(Main.Constants.GSON.toJson(new BotConfig()));
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
