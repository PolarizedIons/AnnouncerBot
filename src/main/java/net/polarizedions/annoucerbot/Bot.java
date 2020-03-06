package net.polarizedions.annoucerbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class Bot {
    private static final Logger log = LogManager.getLogger(Bot.class.getSimpleName());

    private final DiscordClient client;
    private final EventListener eventListener;
    private final Instant startTime;

    public Bot() {
        log.info("Construction a new bot...");
        this.startTime = Instant.now();

        log.debug("Creating client");
        this.client = new DiscordClientBuilder(BotConfig.getInstance().discordToken).build();

        log.debug("Registering events");
        this.eventListener = new EventListener(this);

    }

    public void start() {
        log.info("Logging in...");
        this.client.login().block();
    }

    public DiscordClient getClient() {
        return this.client;
    }
}
