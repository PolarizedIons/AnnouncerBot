package net.polarizedions.annoucerbot;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventListener {
    private static final Logger log = LogManager.getLogger(EventListener.class.getSimpleName());

    private final Bot bot;

    public EventListener(Bot bot) {
        this.bot = bot;

        EventDispatcher ed = bot.getClient().getEventDispatcher();

        ed.on(ReadyEvent.class).subscribe(this::onReadyEvent);
        ed.on(MessageCreateEvent.class).subscribe(this::onMessageEvent);
    }

    private void onReadyEvent(ReadyEvent event) {
        log.info("Logged in as " + event.getSelf().getUsername() + "#" + event.getSelf().getDiscriminator());
    }

    private void onMessageEvent(MessageCreateEvent event) {
        log.debug("Message received: {}", event.getMessage());
    }


}
