package net.polarizedions.annoucerbot.api;

import com.google.gson.reflect.TypeToken;
import net.polarizedions.annoucerbot.api.apiutils.HTTPRequest;
import net.polarizedions.annoucerbot.utils.Constants;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class GithubApi {
    private static final String GITHUB_RELEASE_URL ="https://api.github.com/repos/%s/%s/releases";
    private static final Type releaseListType = new TypeToken<LinkedList<Release>>(){}.getType();

    public static List<Release> getReleases(String user, String repo) {
        String body = HTTPRequest.GET(String.format(GITHUB_RELEASE_URL, user, repo))
                .doRequest()
                .getBody();

        return Constants.GSON.fromJson(body, releaseListType);
    }

    public static class Release {
        public String html_url;
        public long id;
        public String name;
        public String body;
        public boolean prerelease;
    }
}
