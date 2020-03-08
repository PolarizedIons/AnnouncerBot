package net.polarizedions.annoucerbot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class Constants {
    public static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    public static final JsonParser JSON_PARSER = new JsonParser();
}
