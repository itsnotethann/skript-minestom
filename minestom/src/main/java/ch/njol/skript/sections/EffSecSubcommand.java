package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.structures.command.StructCommand;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.List;

public class EffSecSubcommand extends EffectSection {

	static {
		Skript.registerSection(EffSecSubcommand.class, /*"subcommand from name %*string%", */"subcommand <.+>");
	}

	private StructCommand command;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		/*if (matchedPattern == 1) {*/
			String toParse = parseResult.regexes.getFirst().group().replaceFirst("/", "");
			EntryContainer container = StructCommand.COMMAND_VALIDATOR.validate(sectionNode);
			command = new StructCommand(container, toParse);
			return container != null;
		/*}
		String s = ((Literal<String>) expressions[0]).getSingle().replaceFirst("/", "");
		Command command = MinecraftServer.getCommandManager().getCommand(s);
		if (command == null) {
			Skript.error("No command was found under '" + s + "'.");
			return false;
		}
		this.command = new StructCommand(command);
		return true;*/
	}

	public StructCommand getCommand() {
		return command;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "subcommand";
	}

}
