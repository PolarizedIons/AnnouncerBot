package net.polarizedions.annoucerbot.trackers;

import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.trackers.impl.GithubReleasesTracker;
import net.polarizedions.annoucerbot.trackers.impl.MinecraftUpdateTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrackerManager {
    private static final Logger log = LogManager.getLogger(TrackerManager.class.getSimpleName());
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 8));
    private Map<String, ITracker> trackers = new HashMap<>();

    public TrackerManager(Bot bot) {
        ITracker[] trackers = new ITracker[]{
                new MinecraftUpdateTracker(),
                new GithubReleasesTracker(),
        };

        for (ITracker tracker : trackers) {
            this.trackers.put(tracker.getName(), tracker);
            tracker.startup(bot);
        }
    }

    public void startThreadPool() {
        for (ITracker tracker : this.trackers.values()) {
            threadPool.scheduleAtFixedRate(() -> {
                try {
                    tracker.run();
                }
                catch (Exception e) {
                    log.error("Error running tracker {}", tracker.getName(), e);
                }
            }, 5, tracker.getInterval(), TimeUnit.SECONDS);
        }
    }

    public Map<String, ITracker> getTrackers() {
        return this.trackers;
    }

    public void shutdown() {
        this.threadPool.shutdownNow();

        for (ITracker tracker : this.trackers.values()) {
            tracker.shutdown();
        }
    }
}
