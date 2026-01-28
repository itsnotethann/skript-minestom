package com.github.hapily04.skriptminestom.command.reload;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.log.RedirectingLogHandler;
import ch.njol.skript.log.TimingLogHandler;
import ch.njol.util.OpenCloseable;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;

import static com.github.hapily04.skriptminestom.util.MessageUtils.SKRIPT_MINI_MESSAGE;

public class ReloadCommand extends Command {

	private static final Component RELOAD_USAGE = SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <error_color>Usage: /skript reload <all/folder/file/config>");

	public ReloadCommand() {
		super("reload");
		setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.reload"));
		setDefaultExecutor((sender, context) -> sender.sendMessage(RELOAD_USAGE));
		//addSubcommand(new AllCommand());
		//addSubcommand(new ConfigCommand());
		Argument<String[]> folderFileArg = new ArgumentStringArray("to_reload")
			.setSuggestionCallback((sender, context, suggestion) -> {
				suggestion.addEntry(new SuggestionEntry("all"));
				suggestion.addEntry(new SuggestionEntry("config"));
				File scriptsFolder = Skript.getInstance().getScriptsFolder();
				initSuggestions(scriptsFolder.listFiles(), scriptsFolder.getPath().length(), suggestion, true,
					false, false);
			});
		addSyntax((sender, context) -> {
			String locationProvided = context.get(folderFileArg)[0];
			String originalProvidedLocation = locationProvided;
			locationProvided = locationProvided.replace('/', File.separatorChar);
			locationProvided = locationProvided.replace('\\', File.separatorChar);
			if (locationProvided.equalsIgnoreCase("all")) {
				if (!LuckPermsPlayer.hasPermission(sender, "skript.reload.all")) return;
				reloadingMessage(sender, "all scripts and config");
				try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
					SkriptConfig.load();
					ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
					ScriptLoader.loadScripts(Skript.getInstance().getScriptsFolder(), OpenCloseable.combine(new RedirectingLogHandler(sender, null), timingLogHandler))
								.whenComplete((scriptInfo, throwable) -> reloadedMessage(sender, timingLogHandler, "all scripts and config"));
				}
			} else if (locationProvided.equalsIgnoreCase("config")) {
				if (!LuckPermsPlayer.hasPermission(sender, "skript.reload.config")) return;
				reloadingMessage(sender, "config");
				try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
					SkriptConfig.load();
					reloadedMessage(sender, timingLogHandler, "config");
				}
			} else {
				if (!LuckPermsPlayer.hasPermission(sender, "skript.reload.scripts")) return;
				File scriptFile = ScriptLoader.getScriptFromName(locationProvided);
				if (scriptFile == null) {
					fileNotFoundMessage(sender, originalProvidedLocation);
					return;
				}
				boolean directory = scriptFile.isDirectory();
				reloadingMessage(sender, directory ? "scripts in " + originalProvidedLocation : originalProvidedLocation);
				try (TimingLogHandler timingLogHandler = new TimingLogHandler().start()) {
					try (RedirectingLogHandler redirectingLogHandler = new RedirectingLogHandler(sender, null).start()) {
						if (directory) {
							ScriptLoader.unloadScripts(ScriptLoader.getScripts(scriptFile));
							ScriptLoader.loadScripts(scriptFile, OpenCloseable.combine(redirectingLogHandler, timingLogHandler))
										.whenComplete((scriptInfo, throwable) -> {
											reloadedMessage(sender, timingLogHandler, "scripts in " + originalProvidedLocation);
										});
							return;
						}
						Script script = ScriptLoader.getScript(scriptFile);
						if (script != null) ScriptLoader.unloadScript(script);
						ScriptLoader.loadScripts(scriptFile, OpenCloseable.combine(redirectingLogHandler, timingLogHandler))
									.whenComplete((scriptInfo, throwable) -> {
										reloadedMessage(sender, timingLogHandler, originalProvidedLocation);
									});
					}
				}
			}
		}, folderFileArg);
	}

	public static void initSuggestions(File @Nullable [] files, int folderPathLength, @NotNull Suggestion suggestion,
									   boolean includingFolders, boolean includingEnabledScripts, boolean includingDisabledScripts) {
		if (files == null) return;
		for (File file : files) {
			if (file.isHidden()) continue;
			String fileString = file.toString().substring(folderPathLength);
			if (fileString.isBlank()) continue;
			if (file.isDirectory()) {
				initSuggestions(file.listFiles(), folderPathLength, suggestion, includingFolders, includingEnabledScripts, includingDisabledScripts);
				if (!includingFolders) return;
				fileString += File.separator;
			} else {
				boolean isEnabled = ScriptLoader.getScript(file) != null;
				if (includingEnabledScripts && !isEnabled) continue;
				if (includingDisabledScripts && isEnabled) continue;
				fileString = fileString.substring(1);
			}
			if (fileString.contains(".") && !fileString.endsWith(".sk")) continue;
			fileString = fileString.replace(File.separatorChar, '/');
			suggestion.addEntry(new SuggestionEntry(fileString));
		}
	}

	public static void fileNotFoundMessage(CommandSender sender, String fileInQuestion) {
		sender.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <error_color>File '<yellow>" + fileInQuestion + "<error_color>' was not found in the scripts folder."));
	}

	static void reloadingMessage(CommandSender sender, String whatToReload) {
		sender.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <base_grey>Reloading <yellow>" + whatToReload + "<base_grey>..."));
	}

	static void reloadedMessage(CommandSender sender, TimingLogHandler timingLogHandler, String whatToReload) {
		long time = timingLogHandler.getTimeTaken();
		sender.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <success_color>Successfully reloaded <yellow>" + whatToReload + " <success_color>in " + time + "ms."));
	}

}
