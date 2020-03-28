package net.polarizedions.annoucerbot.bot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import net.polarizedions.annoucerbot.commands.CommandManager;
import net.polarizedions.annoucerbot.trackers.TrackerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class Bot {
    private static final Logger log = LogManager.getLogger(Bot.class.getSimpleName());

    private final DiscordClient client;
    private final EventListener eventListener;
    private final CommandManager commandManager;
    private final TrackerManager trackerManager;

    public Bot() {
        log.info("Construction a new bot...");

        log.debug("Creating client");
        this.client = new DiscordClientBuilder(BotConfig.getInstance().discordToken).build();

        log.debug("Registering events");
        this.eventListener = new EventListener(this);

        log.debug("Registering Trackers");
        this.trackerManager = new TrackerManager(this);

        log.debug("Registering commands");
        this.commandManager = new CommandManager(this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownHook));
    }

    public void start() {
        log.info("Logging in...");
        this.client.login().block();
    }

    private void shutdownHook() {
        log.info("Shutting down...");
        this.trackerManager.shutdown();
        this.client.logout().block();
    }

    public DiscordClient getClient() {
        return this.client;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }
}
