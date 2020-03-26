package net.polarizedions.annoucerbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.annoucerbot.bot.Bot;

public interface ICommand {
    default void init(Bot bot) {
    }

    void register(CommandDispatcher<CommandSource> dispatcher);
}
