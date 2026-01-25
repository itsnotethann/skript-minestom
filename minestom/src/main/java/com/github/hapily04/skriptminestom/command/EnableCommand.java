package com.github.hapily04.skriptminestom.command;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.log.RedirectingLogHandler;
import ch.njol.skript.log.TimingLogHandler;
import ch.njol.util.OpenCloseable;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;

import java.io.File;

import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.fileNotFoundMessage;
import static com.github.hapily04.skriptminestom.command.reload.ReloadCommand.initSuggestions;
import static com.github.hapily04.skriptminestom.util.MessageUtils.SKRIPT_MINI_MESSAGE;

public class EnableCommand extends Command {

	private static final Component ENABLE_USAGE = SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <error_color>Usage: /skript enable <file>");

	public EnableCommand() {
		super("enable");
		setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.enable"));
		setDefaultExecutor((sender, context) -> sender.sendMessage(ENABLE_USAGE));
		Argument<String[]> fileArg = new ArgumentStringArray("to_enable")
			.setSuggestionCallback((sender, context, suggestion) -> {
				File scriptsFolder = Skript.getInstance().getScriptsFolder();
				initSuggestions(scriptsFolder.listFiles(), scriptsFolder.getPath().length(), suggestion,
					false, false, true);
			});
		addSyntax((sender, context) -> {
			String locationProvided = context.get(fileArg)[0];
			String originalProvidedLocation = locationProvided;
			locationProvided = locationProvided.replace('/', File.separatorChar);
			locationProvided = locationProvided.replace('\\', File.separatorChar);
			File scriptFile = ScriptLoader.getScriptFromName(locationProvided);
			if (scriptFile == null) {
				fileNotFoundMessage(sender, originalProvidedLocation);
				return;
			}
			if (ScriptLoader.getScript(scriptFile) != null) {
				sender.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <yellow>" + originalProvidedLocation + " <error_color>is already enabled."));
				return;
			}
			try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
				ScriptLoader.loadScripts(scriptFile, OpenCloseable.combine(new RedirectingLogHandler(sender, null), timingLogHandler))
							.whenComplete((scriptInfo, throwable) -> {
								long time = timingLogHandler.getTimeTaken();
								sender.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <success_color>Successfully enabled <yellow>" + originalProvidedLocation + " <success_color>in " + time + "ms."));
							});
			}
		}, fileArg);
	}

}
