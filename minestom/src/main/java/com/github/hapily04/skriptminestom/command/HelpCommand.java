package com.github.hapily04.skriptminestom.command;

import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;

import static com.github.hapily04.skriptminestom.command.SkriptCommand.HELP_MESSAGE;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help");
		setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.skript"));
		setDefaultExecutor((sender, context) -> sender.sendMessage(HELP_MESSAGE));
	}

}
