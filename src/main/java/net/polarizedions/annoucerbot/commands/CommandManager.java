package net.polarizedions.annoucerbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.object.entity.User;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.bot.BotConfig;
import net.polarizedions.annoucerbot.commands.impl.Info;

public class CommandManager {
    private CommandDispatcher<CommandSource> dispatcher;

    public CommandManager() {
        ICommand[] commands = new ICommand[]{
                new Info()
        };

        this.dispatcher = new CommandDispatcher<>();

        for (ICommand cmd : commands) {
            cmd.register(this.dispatcher);
        }
    }

    public void processMessage(CommandSource source) {
        User user = source.getUser().orElse(null);
        if (user != null && user.isBot()) {
            return;
        }

        String botPrefix = BotConfig.getInstance().prefix;
        if (source.getMessage().startsWith(botPrefix)) {
            try {
                dispatcher.execute(source.getMessage().substring(botPrefix.length()), source);
            }
            catch (CommandSyntaxException e) {
                // NOOP
            }
        }
    }
}
