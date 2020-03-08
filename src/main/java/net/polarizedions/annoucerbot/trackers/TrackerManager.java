package net.polarizedions.annoucerbot.trackers;

import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.trackers.impl.MinecraftUpdateTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrackerManager {
    private Map<String, ITracker> trackers = new HashMap<>();
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 8));

    public TrackerManager(Bot bot) {
        ITracker[] trackers = new ITracker[] {
                new MinecraftUpdateTracker(),
        };

        for (ITracker tracker : trackers) {
            this.trackers.put(tracker.getName(), tracker);
            tracker.startup(bot);
        }
    }

    public void startThreadPool() {
        for (ITracker tracker : this.trackers.values()) {
            threadPool.scheduleAtFixedRate(tracker::run, 5, tracker.getInterval(), TimeUnit.SECONDS);
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
