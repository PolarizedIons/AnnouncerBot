package net.polarizedions.annoucerbot.commands;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import net.polarizedions.annoucerbot.bot.Bot;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

public class CommandSource {
    private Message message;
    private boolean privateMessage;
    private Snowflake guildId;
    private Bot bot;

    public CommandSource(Bot bot, MessageCreateEvent event) {
        this.bot = bot;
        this.message = event.getMessage();
        this.privateMessage = event.getGuildId().isEmpty();
        this.guildId = event.getGuildId().isPresent() ? event.getGuildId().get() : null;
    }

    public String getMessage() {
        return this.message.getContent().isPresent() ? this.message.getContent().get() : "";
    }

    public boolean isPrivateMessage() {
        return this.privateMessage;
    }

    public Message getWrapped() {
        return this.message;
    }

    public void reply(String text) {
        this.message.getChannel().subscribe(channel -> channel.createMessage(text).subscribe());
    }

    public void replyEmbed(Consumer<EmbedCreateSpec> specConsumer) {
        this.message.getChannel().subscribe(channel -> channel.createEmbed(specConsumer).subscribe());
    }

    public Optional<User> getUser() {
        return this.message.getAuthor();
    }

    public Mono<MessageChannel> getChannel() {
        return this.message.getChannel();
    }

    public Bot getBot() {
        return this.bot;
    }

    public DiscordClient getClient() {
        return this.getBot().getClient();
    }

    public Snowflake getGuildId() {
        return this.guildId;
    }
}