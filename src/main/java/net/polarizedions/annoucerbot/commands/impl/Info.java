package net.polarizedions.annoucerbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.annoucerbot.commands.CommandSource;
import net.polarizedions.annoucerbot.commands.ICommand;
import net.polarizedions.annoucerbot.commands.brigadier.BrigadierTypeFixer;

import static net.polarizedions.annoucerbot.commands.brigadier.BrigadierTypeFixer.literal;

public class Info implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            literal("info").executes(c -> this.info(c.getSource()))
        );
    }

    private int info(CommandSource source) {
        source.reply("Info!!!");
        return 1;
    }
}
