package net.polarizedions.annoucerbot;

import com.google.gson.Gson;
import net.polarizedions.annoucerbot.bot.Bot;

public class Main {
    public static void main(String[] args) {
        new Bot().start();
    }

    public static class Constants {
        public static Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    }
}
