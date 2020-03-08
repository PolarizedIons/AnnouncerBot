package net.polarizedions.annoucerbot.trackers.impl;

import com.google.gson.JsonElement;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.annoucerbot.api.MinecraftApi;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.trackers.ITracker;
import net.polarizedions.annoucerbot.utils.Colours;
import net.polarizedions.annoucerbot.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinecraftUpdateTracker implements ITracker {
    private static final Logger log = LogManager.getLogger(MinecraftUpdateTracker.class.getSimpleName());

    private Bot bot;
    private File configFile;
    private List<Long> channels = new ArrayList<>();

    private MinecraftApi.LatestVersions latestVersions = null;

    @Override
    public String getName() {
        return "minecraft";
    }

    @Override
    public String getDescription() {
        return "Minecraft Update tracker";
    }

    @Override
    public long getInterval() {
        return 60;
    }

    @Override
    public void startup(Bot bot) {
        this.configFile = new File("config/mcupdates.json");
        this.bot = bot;

        try {
            if (this.configFile.exists()) {
                for (JsonElement el : Constants.JSON_PARSER.parse(new FileReader(this.configFile)).getAsJsonArray()) {
                    this.channels.add(el.getAsLong());
                }
            }
        } catch (IOException e) {
            // NOOP
        }
    }

    @Override
    public void run() {
        System.out.println("run called");
        MinecraftApi.LatestVersions latest = MinecraftApi.getLatestVersions();

        if (this.latestVersions != null) {
            if (!this.latestVersions.release.equals(latest.release)) {
                this.announce("release", latest.release);
            }
            else if (!this.latestVersions.snapshot.equals(latest.snapshot)) {
                this.announce("snapshot", latest.snapshot);
            }
        }

        this.latestVersions = latest;
    }

    private void announce(String type, String version) {
        for (Long channelId : this.channels) {
            this.bot.getClient().getChannelById(Snowflake.of(channelId)).subscribe((channel -> {
                if (channel instanceof TextChannel) {
                    System.out.println("Sending to channel " + channel);
                    ((TextChannel) channel).createEmbed(spec -> {
                        spec.setTitle(this.getDescription());

                        spec.addField("New " + type, version, false);

                        spec.setColor(Colours.GOOD);
                        spec.setFooter("Automated", null);
                    }).subscribe();
                }
            }));
        }
    }

    @Override
    public void shutdown() {
        try {
            FileWriter fw = new FileWriter(this.configFile);
            fw.write(Constants.GSON.toJson(this.channels));
            fw.close();
        } catch (IOException e) {
            log.error("Error saving config file", e);
        }

    }

    @Override
    public boolean addChannel(Channel channel, String[] args) {
        this.channels.add(channel.getId().asLong());
        return true;
    }

    @Override
    public boolean removeChannel(Channel channel, String[] args) {
        this.channels.remove(channel.getId().asLong());
        return true;
    }
}
