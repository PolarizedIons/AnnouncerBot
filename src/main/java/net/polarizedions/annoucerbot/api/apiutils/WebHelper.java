package net.polarizedions.annoucerbot.api.apiutils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebHelper {
    // From: https://stackoverflow.com/a/14424783
    public static String encodeURIComponent(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
    }
}