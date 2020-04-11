package net.polarizedions.annoucerbot.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.annoucerbot.api.apiutils.HTTPRequest;
import net.polarizedions.annoucerbot.api.apiutils.WebHelper;
import net.polarizedions.annoucerbot.bot.BotConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchApi {
    private static final int STREAMS_REQUEST_SIZE = 100;
    private static final String TWITCH_STEAMS_URL = "https://api.twitch.tv/helix/streams?";
    private static final String TWITCH_STREAM_USER_PART = "user_login=%s";

    public static Map<String, LiveResponse> isStreaming(List<String> users) {
        List<List<String>> toCheck = new ArrayList<>();
        for (int i = 0; i < users.size(); i += STREAMS_REQUEST_SIZE) {
            toCheck.add(users.subList(i, Math.min(users.size(), i + STREAMS_REQUEST_SIZE)));
        }
        Map<String, LiveResponse> result = new HashMap<>();

        for (List<String> chunk : toCheck) {
            StringBuilder urlBuilder = new StringBuilder(TWITCH_STEAMS_URL);

            for (String user : chunk) {
                urlBuilder.append(String.format(TWITCH_STREAM_USER_PART, WebHelper.encodeURIComponent(user))).append("&");
            }

            String url = urlBuilder.toString();
            JsonObject obj = HTTPRequest.GET(url.substring(0, url.length() - 1))
                    .setHeader("Client-ID", BotConfig.getInstance().twitchClientID)
                    .doRequest()
                    .asJsonObject();

            Map<String, String> onlineTitles = new HashMap<>();
            for (JsonElement el : obj.getAsJsonArray("data")) {
                onlineTitles.put(el.getAsJsonObject().get("user_name").getAsString().toLowerCase(), el.getAsJsonObject().get("title").getAsString());
            }

            for (String user : chunk) {
                LiveResponse liveResponse = new LiveResponse();
                liveResponse.isLive = onlineTitles.containsKey(user);
                liveResponse.username = user;
                liveResponse.title = onlineTitles.getOrDefault(user, "");
                result.put(user, liveResponse);
            }
        }

        return result;
    }

    public static class LiveResponse {
        public boolean isLive;
        public String username;
        public String title;
    }
}
