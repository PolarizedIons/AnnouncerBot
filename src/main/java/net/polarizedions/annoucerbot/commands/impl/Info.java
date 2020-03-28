package net.polarizedions.annoucerbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.commands.ICommand;
import net.polarizedions.annoucerbot.utils.BuildInfo;
import net.polarizedions.annoucerbot.utils.Colours;
import net.polarizedions.annoucerbot.utils.Uptime;

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
        source.getBot().getClient().getSelf().subscribe(ourUser -> {
            source.replyEmbed(spec -> {
                spec.setTitle("Bot Info");

                spec.addField("Name", ourUser.getUsername() + "#" + ourUser.getDiscriminator(), true);
                spec.addField("Version", BuildInfo.version, true);
                spec.addField("Built at", BuildInfo.buildtime, true);
                spec.addField("Uptime", Uptime.get(), true);
                spec.addField("Java version", System.getProperty("java.version"), true);

                spec.setColor(Colours.GOOD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
        });

        return 1;
    }
}
