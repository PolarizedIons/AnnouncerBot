package net.polarizedions.annoucerbot.trackers.impl;

import com.google.gson.reflect.TypeToken;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.annoucerbot.api.GithubApi;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.trackers.ITracker;
import net.polarizedions.annoucerbot.utils.Colours;
import net.polarizedions.annoucerbot.utils.Constants;
import net.polarizedions.annoucerbot.utils.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class GithubReleasesTracker implements ITracker {
    private static final Logger log = LogManager.getLogger(GithubReleasesTracker.class.getSimpleName());
    private static final File CONFIG_FILE = new File("./config/github.json");
    private static final Type CONFIG_TYPE = new TypeToken<Map<String, List<Long>>>(){}.getType();
    private Map<Pair<String, String>, List<Long>> tracking = new HashMap<>();
    private Map<Pair<String, String>, Long> lastKnownRelease = new HashMap<>();
    private Bot bot;

    @Override
    public String getName() {
        return "ghrelease";
    }

    @Override
    public String getDescription() {
        return "Github Releases";
    }

    @Override
    public long getInterval() {
        return 300;
    }

    @Override
    public void run() {
         for (Map.Entry<Pair<String, String>, List<Long>> sub : this.tracking.entrySet()) {
            List<GithubApi.Release> releases = getReleases(sub.getKey());
            Long newLastRelease = releases.size() == 0 ? null : releases.get(0).id;
             for (GithubApi.Release release : releases) {
                if (lastKnownRelease.get(sub.getKey()) == null) {
                    break;
                }

                if (release.id == lastKnownRelease.get(sub.getKey())) {
                    break;
                }

                for (Long id : sub.getValue()) {
                    this.bot.getClient().getChannelById(Snowflake.of(id)).subscribe(channel -> {
                        if (channel instanceof TextChannel) {
                            ((TextChannel) channel).createEmbed(spec -> {
                                String target = sub.getKey().one + "/" + sub.getKey().two;
                                String body = release.body;
                                body = body.isEmpty() ? "-" : body;
                                body = body.length() >= 1024 ? body.substring(0, 1023) : body;

                                spec.setAuthor(target, "https://github.com/" + target, null);
                                spec.setTitle((release.prerelease ? "New Pre-release" : "New Release") + ": " + release.name);
                                spec.setUrl(release.html_url);
                                spec.setDescription(body);

                                spec.setColor(Colours.GOOD);
                                spec.setFooter("Automated", null);
                            }).subscribe();
                        } else {
                            this.remove(sub.getKey(), id);
                        }
                    });
                }
            }

            this.lastKnownRelease.put(sub.getKey(), newLastRelease);
        }
    }

    @Override
    public boolean addChannel(CommandSource source, String[] args) {
        if (args.length != 1) {
            source.replyEmbed(spec -> {
                spec.setTitle(this.getDescription());
                spec.addField("No arguments", "You must specific who to subscribe from!", false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
            return false;
        }

        String[] target = args[0].split("/");
        if (target.length != 2) {
            source.replyEmbed(spec -> {
                spec.addField("Wrong number of arguments", "You must specific who to subscribe to!",  false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
            return false;
        }

        Pair<String, String> targetPair = new Pair<>(target[0], target[1]);
        source.getChannel().subscribe(channel -> {
            this.tracking.computeIfAbsent(targetPair, k -> new ArrayList<>());
            this.tracking.get(targetPair).add(channel.getId().asLong());
        });

        return true;
    }

    @Override
    public boolean removeChannel(CommandSource source, String[] args) {
        if (args.length != 1) {
            source.replyEmbed(spec -> {
                spec.setTitle("No arguments");
                spec.addField("You must specific who to unsubscribe from!", "", false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
            return false;
        }

        String[] target = args[0].split("/");
        if (target.length != 2) {
            source.replyEmbed(spec -> {
                spec.setTitle("Wrong number of arguments");
                spec.addField("You must specific who to unsubscribe from!", "", false);

                spec.setColor(Colours.BAD);
                AtomicReference<String> requestedBy = new AtomicReference<>();
                source.getUser().ifPresentOrElse((user) -> requestedBy.set(user.getUsername() + "#" + user.getDiscriminator()), () -> requestedBy.set("unknown"));
                spec.setFooter("requested by " + requestedBy.get(), null);
            });
            return false;
        }

        source.getChannel().subscribe(channel -> {
            this.remove(new Pair<>(target[0], target[1]), channel.getId().asLong());
        });

        return true;
    }

    @Override
    public void startup(Bot bot) {
        this.bot = bot;

        if (CONFIG_FILE.exists()) {
            try {
                Map<String, List<Long>> config = Constants.GSON.fromJson(new FileReader(CONFIG_FILE), CONFIG_TYPE);

                for (Map.Entry<String, List<Long>> entry : config.entrySet()) {
                    String[] key = entry.getKey().split("/");
                    this.tracking.put(new Pair<>(key[0], key[1]), entry.getValue());
                }
            } catch (FileNotFoundException e) {
                log.error("Error loading file", e);
            }
        }
    }

    @Override
    public void shutdown() {
        Map<String, List<Long>> config = new HashMap<>();
        for (Map.Entry<Pair<String, String>, List<Long>> entry : this.tracking.entrySet()) {
            config.put(entry.getKey().one + "/" + entry.getKey().two, entry.getValue());
        }

        try {
            FileWriter fw = new FileWriter(CONFIG_FILE);
            fw.write(Constants.GSON.toJson(config));
            fw.close();
        } catch (IOException e) {
            log.error("Error writing config file", e);
        }
    }

    private void remove(Pair<String, String> target, Long id) {
        this.tracking.get(target).remove(id);
        if (this.tracking.get(target).size() == 0) {
            this.tracking.remove(target);
        }
    }

    private static List<GithubApi.Release> getReleases(Pair<String, String> userAndRepo) {
        return GithubApi.getReleases(userAndRepo.one, userAndRepo.two);
    }
}
