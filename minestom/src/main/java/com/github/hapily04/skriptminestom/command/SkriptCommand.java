package com.github.hapily04.skriptminestom.command;

import com.github.hapily04.skriptminestom.command.reload.ReloadCommand;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;

import static com.github.hapily04.skriptminestom.util.MessageUtils.SKRIPT_MINI_MESSAGE;

public class SkriptCommand extends Command {

	static final Component HELP_MESSAGE = SKRIPT_MINI_MESSAGE.deserialize("""
		<skript_minestom_tag> <base_grey>Help
		<yellow>reload <all/folder/file/config> <base_grey>- Reload a scripts folder, script file, or the Skript config.
		<yellow>disable <file> <base_grey>- Disable and unload an enabled script file.
		<yellow>enable <file> <base_grey>- Enable and load a disabled script file.
		<yellow>help <base_grey>- Show this help message.""");
		//<yellow>info <base_grey>- Show addon information & server information.""");

    public SkriptCommand() {
        super("skript", "sk");
        setCondition((sender, commandString) ->  LuckPermsPlayer.hasPermission(sender, "skript.skript"));
        setDefaultExecutor((sender, context) -> sender.sendMessage(HELP_MESSAGE));
        addSubcommand(new ReloadCommand());
		addSubcommand(new DisableCommand());
		addSubcommand(new EnableCommand());
		addSubcommand(new HelpCommand());
	}

}
