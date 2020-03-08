package net.polarizedions.annoucerbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.commands.ICommand;
import net.polarizedions.annoucerbot.utils.Colours;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static net.polarizedions.annoucerbot.commands.brigadier.BrigadierTypeFixer.literal;

public class Info implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            literal("info").executes(c -> this.info(c.getSource()))
        );
    }

    private int info(CommandSource source) {
        source.replyEmbed(spec -> {
            spec.setTitle("Bot Info");

            spec.addField("Uptime", this.getUptime(source.getBot()), true);

            spec.setColor(Colours.GOOD);
            AtomicReference<String> requestedBy = new AtomicReference<>();
            source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
            spec.setFooter("requested by " + requestedBy.get(), null);
        });
        return 1;
    }

    private String getUptime(Bot bot) {
        Duration diff = Duration.between(bot.getStartTime(), Instant.now());
        long weeks = diff.toDays() / 7;
        long days = diff.toDays() % 7;
        long hours = diff.toHours() % 24;
        long minutes = diff.toMinutes() % 60;
        long seconds = diff.toSeconds() % 60;

        return weeks + (weeks == 1 ? " week " : " weeks ")
                + days + (days == 1 ? " day " : " days ")
                + hours + (hours == 1 ? " hour " : " hours ")
                + minutes + (minutes == 1 ? " minute " : " minutes ")
                + seconds + (seconds == 1 ? " second" : " seconds");
    }
}
