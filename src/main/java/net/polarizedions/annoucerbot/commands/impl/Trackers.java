package net.polarizedions.annoucerbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.bot.BotConfig;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.commands.ICommand;
import net.polarizedions.annoucerbot.trackers.ITracker;
import net.polarizedions.annoucerbot.utils.Colours;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.annoucerbot.commands.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.annoucerbot.commands.brigadier.BrigadierTypeFixer.literal;

public class Trackers implements ICommand {
    private static final Logger log = LogManager.getLogger(Trackers.class.getSimpleName());

    private List<ITracker> trackers;

    @Override
    public void init(Bot bot) {
        this.trackers = new ArrayList<>();
        this.trackers.addAll(bot.getTrackerManager().getTrackers().values());
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        for (ITracker tracker : this.trackers) {
            log.debug("Registering tracker command {}", tracker.getName());
            dispatcher.register(
                    literal(tracker.getName())
                            .then(
                                    literal("add")
                                            .then(
                                                    argument("args", greedyString()).executes(c -> this.add(tracker, c.getSource(), getString(c, "args")))
                                            )
                                            .executes(c -> this.add(tracker, c.getSource(), ""))
                            )
                            .then(
                                    literal("remove")
                                            .then(
                                                    argument("args", greedyString()).executes(c -> this.remove(tracker, c.getSource(), getString(c, "args")))
                                            )
                                            .executes(c -> this.remove(tracker, c.getSource(), ""))
                            )
                            .executes(c -> this.help(tracker, c.getSource()))
            );
        }

        dispatcher.register(
                literal("trackers").executes(c -> this.list(c.getSource()))
        );
    }

    private int list(CommandSource source) {
        source.replyEmbed(spec -> {
            spec.setTitle("Trackers:");

            for (ITracker tracker : this.trackers) {
                spec.addField(tracker.getName(), tracker.getDescription(), true);
            }

            spec.setColor(Colours.GOOD);
            AtomicReference<String> requestedBy = new AtomicReference<>();
            source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
            spec.setFooter("requested by " + requestedBy.get(), null);
        });
        return 1;
    }

    private int add(ITracker tracker, CommandSource source, String args) {
        if (tracker.addChannel(source, args.trim().split("\\s", -1))) {
            source.replyEmbed(spec -> {
                spec.setTitle(tracker.getDescription());

                spec.addField("Subscribed!", "Successfully subscribed this channel", false);

                spec.setColor(Colours.GOOD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
        }
        return 1;
    }

    private int remove(ITracker tracker, CommandSource source, String args) {
        if (tracker.removeChannel(source, args.trim().split("\\s", -1))) {
            source.replyEmbed(spec -> {
                spec.setTitle(tracker.getDescription());

                spec.addField("Unsubscribed!", "Successfully unsubscribed this channel", false);

                spec.setColor(Colours.GOOD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
        };
        return 1;
    }

    private int help(ITracker tracker, CommandSource source) {
        String prefix = BotConfig.getInstance().prefix;

        source.replyEmbed(spec -> {
            spec.setTitle(tracker.getDescription() + " help");

            spec.addField(prefix + tracker.getName() + " add [args]", "Adds this tracker to the channel", false);
            spec.addField(prefix + tracker.getName() + " remove [args]", "Removes this tracker from the channel", false);

            spec.setColor(Colours.GOOD);
            AtomicReference<String> requestedBy = new AtomicReference<>();
            source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
            spec.setFooter("requested by " + requestedBy.get(), null);
        });


        return 1;
    }
}
