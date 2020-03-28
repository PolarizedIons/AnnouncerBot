package net.polarizedions.annoucerbot.utils;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Uptime {
    private static final String[] words = new String[] {
            "weeks",
            "days",
            "hours",
            "minutes",
            "seconds"
    };
    private static final String[] singularWords = new String[] {
            "week",
            "day",
            "hour",
            "minute",
            "second"
    };

    private static Instant startTime;

    public static void start() {
        startTime = Instant.now();
    }

    private static String formatTime(Duration duration) {
        long[] time = new long[5];
        time[0] = duration.toDays() / 7;
        time[1] = duration.toDays() % 7;
        time[2] = duration.toHours() % TimeUnit.DAYS.toHours(1);
        time[3] = duration.toMinutes() % TimeUnit.HOURS.toMinutes(1);
        time[4] = duration.getSeconds() % TimeUnit.MINUTES.toSeconds(1);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < time.length; i++) {
            if (time[i] == 0 && formatted.length() == 0) {
                continue;
            }

            formatted.append(time[i]).append(" ").append(time[i] == 1 ? singularWords[i] : words[i]).append(" ");
        }

        return formatted.toString().trim();
    }

    public static String get() {
        return formatTime(Duration.between(startTime, Instant.now()));
    }
}
