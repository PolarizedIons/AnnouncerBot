package net.polarizedions.annoucerbot.commands;

import com.mojang.brigadier.CommandDispatcher;

public interface ICommand {
    void register(CommandDispatcher<CommandSource> dispatcher);
}
