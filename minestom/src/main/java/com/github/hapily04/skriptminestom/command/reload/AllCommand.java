package com.github.hapily04.skriptminestom.command.reload;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.log.RedirectingLogHandler;
import ch.njol.skript.log.TimingLogHandler;
import ch.njol.util.OpenCloseable;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.minestom.server.command.builder.Command;

import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.reloadedMessage;
import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.reloadingMessage;

public class AllCommand extends Command {

	public AllCommand() {
		super("all");

		/*setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.reload.all"));
		setDefaultExecutor((sender, context) -> {
			reloadingMessage(sender, "all scripts and config");
			try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
				SkriptConfig.load();
				ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
				ScriptLoader.loadScripts(Skript.getInstance().getScriptsFolder(), OpenCloseable.combine(new RedirectingLogHandler(sender, null), timingLogHandler))
					.whenComplete((scriptInfo, throwable) -> reloadedMessage(sender, timingLogHandler, "all scripts and config"));
			}
		});*/
	}

}
