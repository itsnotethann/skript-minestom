package com.github.hapily04.skriptminestom.command;

import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.minestom.server.command.builder.Command;

import static com.github.hapily04.skriptminestom.command.SkriptCommand.HELP_MESSAGE;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help");
		setCondition((sender, _) -> LuckPermsPlayer.hasPermission(sender, "skript.skript"));
		setDefaultExecutor((sender, _) -> sender.sendMessage(HELP_MESSAGE));
	}

}
