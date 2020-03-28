package net.polarizedions.annoucerbot;

import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.utils.Uptime;

public class Main {
    public static void main(String[] args) {
        Uptime.start();
        new Bot().start();
    }
}
