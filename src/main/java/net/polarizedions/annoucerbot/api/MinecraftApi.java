package net.polarizedions.annoucerbot.api;

import com.google.gson.JsonObject;
import net.polarizedions.annoucerbot.api.apiutils.HTTPRequest;

public class MinecraftApi {
    private static final String LATEST_VERSION_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    public static LatestVersions getLatestVersions() {
        JsonObject json = HTTPRequest.GET(LATEST_VERSION_URL)
                .doRequest()
                .asJsonObject();

        if (json == null) {
            return new LatestVersions("unknown", "unknown");
        }

        JsonObject latest = json.getAsJsonObject("latest");
        return new LatestVersions(latest.get("release").getAsString(), latest.get("snapshot").getAsString());
    }

    public static class LatestVersions {
        public String release;
        public String snapshot;

        public LatestVersions(String release, String snapshot) {
            this.release = release;
            this.snapshot = snapshot;
        }
    }
}
