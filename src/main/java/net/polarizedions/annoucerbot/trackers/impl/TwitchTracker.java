package net.polarizedions.annoucerbot.trackers.impl;

import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.annoucerbot.api.TwitchApi;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.trackers.ITracker;
import net.polarizedions.annoucerbot.utils.Colours;
import net.polarizedions.annoucerbot.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TwitchTracker implements ITracker {
    private static final Logger log = LogManager.getLogger(TwitchTracker.class.getSimpleName());
    private static final File CONFIG_FILE = new File("./config/twitch.json");

    private State state = new State();
    private Bot bot;

    @Override
    public String getName() {
        return "twitch";
    }

    @Override
    public String getDescription() {
        return "Twitch Streams";
    }

    @Override
    public long getInterval() {
        return 60;
    }

    @Override
    public void run() {
        Map<String, TwitchApi.LiveResponse> streamers = TwitchApi.isStreaming(new ArrayList<>(this.state.trackingChannels.keySet()));
        for (Map.Entry<String, TwitchApi.LiveResponse> streamer : streamers.entrySet()) {
            if (this.state.trackingStreams.get(streamer.getKey()) != null) {
                if (!this.state.trackingStreams.get(streamer.getKey()) && streamer.getValue().isLive) {
                    List<Long> channelIds = this.state.trackingChannels.get(streamer.getKey());
                    for (Long channelId : channelIds) {
                        this.bot.getClient().getChannelById(Snowflake.of(channelId)).subscribe(channel -> {
                            if (channel instanceof TextChannel) {
                                ((TextChannel) channel).createEmbed(spec -> {
                                    spec.setTitle(streamer.getKey() + " is now live!");
                                    spec.setThumbnail("https://static-cdn.jtvnw.net/previews-ttv/live_user_" + streamer.getKey() + ".jpg");
                                    spec.setUrl("https://twitch.tv/" + streamer.getKey());

                                    spec.addField("Title", streamer.getValue().title, false);

                                    spec.setColor(Colours.GOOD);
                                    spec.setFooter("Automated", null);
                                }).subscribe();
                            }
                        });
                    }
                }
            }

            this.state.trackingStreams.put(streamer.getKey(), streamer.getValue().isLive);
        }
    }

    @Override
    public boolean addChannel(CommandSource source, String[] args) {
        if (args.length != 1) {
            source.replyEmbed(spec -> {
                spec.setTitle(this.getDescription());
                spec.addField("Incorrect number of arguments", "You must specific who to subscribe to!", false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });

            return false;
        }

        source.getChannel().subscribe(channel -> {
            String channelName = args[0].toLowerCase();
            this.state.trackingStreams.computeIfAbsent(channelName, k -> null);
            this.state.trackingChannels.computeIfAbsent(channelName, k -> new ArrayList<>());
            this.state.trackingChannels.get(channelName).add(channel.getId().asLong());
        });

        return true;
    }

    @Override
    public boolean removeChannel(CommandSource source, String[] args) {
        if (args.length != 1) {
            source.replyEmbed(spec -> {
                spec.setTitle(this.getDescription());
                spec.addField("Incorrect number of arguments", "You must specific who to unsubscribe from!", false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });

            return false;
        }

        String channelName = args[0].toLowerCase();
        List<Long> channelSubs = this.state.trackingChannels.get(channelName);
        if (channelSubs == null) {
            return false;
        }

        source.getChannel().subscribe(channel -> {
           channelSubs.remove(channel.getId().asLong());
           if (channelSubs.size() == 0) {
               this.state.trackingStreams.remove(channelName);
               this.state.trackingChannels.remove(channelName);
           }
        });

        return true;
    }

    @Override
    public void startup(Bot bot) {
        this.bot = bot;

        if (CONFIG_FILE.exists()) {
            try {
                this.state = Constants.GSON.fromJson(new FileReader(CONFIG_FILE), State.class);
            } catch (FileNotFoundException e) {
                log.error("Error reading config file", e);
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            FileWriter fw = new FileWriter(CONFIG_FILE);
            fw.write(Constants.GSON.toJson(this.state));
            fw.close();
        } catch (IOException e) {
            log.error("Error writing config file", e);
        }
    }

    private static class State {
        public Map<String, Boolean> trackingStreams = new HashMap<>();
        public Map<String, List<Long>> trackingChannels = new HashMap<>();
    }
}
