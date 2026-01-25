package com.github.hapily04.skriptminestom.command.reload;

import ch.njol.skript.SkriptConfig;
import ch.njol.skript.log.TimingLogHandler;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.minestom.server.command.builder.Command;

import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.reloadedMessage;
import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.reloadingMessage;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super("config");
		setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.reload.config"));
		setDefaultExecutor((sender, context) -> {
			reloadingMessage(sender, "config");
			try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
				SkriptConfig.load();
				reloadedMessage(sender, timingLogHandler, "config");
			}
		});
	}

}
