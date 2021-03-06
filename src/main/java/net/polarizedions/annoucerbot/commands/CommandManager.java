package net.polarizedions.annoucerbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.object.entity.User;
import net.polarizedions.annoucerbot.bot.Bot;
import net.polarizedions.annoucerbot.bot.BotConfig;
import net.polarizedions.annoucerbot.commands.impl.Info;
import net.polarizedions.annoucerbot.commands.impl.Trackers;
import net.polarizedions.annoucerbot.utils.Colours;

public class CommandManager {
    private CommandDispatcher<CommandSource> dispatcher;

    public CommandManager(Bot bot) {
        ICommand[] commands = new ICommand[]{
                new Info(),
                new Trackers(),
        };

        this.dispatcher = new CommandDispatcher<>();


        for (ICommand cmd : commands) {
            cmd.init(bot);
        }

        for (ICommand cmd : commands) {
            cmd.register(this.dispatcher);
        }
    }

    public void processMessage(CommandSource source) {
        User user = source.getUser().orElse(null);
        if (user == null || user.isBot()) {
            return;
        }

        if (source.isPrivateMessage()) {
            source.replyEmbed(spec -> {
                spec.setTitle("PMs are not supported");

                spec.addField("Sorry!", "Private Messages are not supported! Please message me in a server!", false);

                spec.setColor(Colours.BAD);
                spec.setFooter("requested by " + user.getUsername() + "#" + user.getDiscriminator(), null);
            });

            return;
        }

        String botPrefix = BotConfig.getInstance().prefix;
        if (source.getMessage().startsWith(botPrefix)) {
            try {
                dispatcher.execute(source.getMessage().substring(botPrefix.length()), source);
            } catch (CommandSyntaxException e) {
                // NOOP
            }
            catch (Exception e) {
                source.reply("Sorry, an error occurred! " + e.getMessage());
            }
        }
    }
}
