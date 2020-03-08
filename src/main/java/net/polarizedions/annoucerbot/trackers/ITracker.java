package net.polarizedions.annoucerbot.trackers;

import discord4j.core.object.entity.Channel;
import net.polarizedions.annoucerbot.bot.Bot;

public interface ITracker {
    String getName();
    String getDescription();
    long getInterval();
    void run();

    boolean addChannel(Channel channel, String[] args);
    boolean removeChannel(Channel channel, String[] args);

    default void startup(Bot bot) {}
    default void shutdown() {}
}
