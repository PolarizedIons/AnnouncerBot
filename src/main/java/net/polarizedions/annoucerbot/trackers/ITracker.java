package net.polarizedions.annoucerbot.trackers;

import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.commands.CommandSource;

public interface ITracker {
    String getName();
    String getDescription();
    long getInterval();
    void run();

    boolean addChannel(CommandSource source, String[] args);
    boolean removeChannel(CommandSource source, String[] args);

    default void startup(Bot bot) {}
    default void shutdown() {}
}
