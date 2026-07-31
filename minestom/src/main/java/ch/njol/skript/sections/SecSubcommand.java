package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.structures.command.StructCommand;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.List;

@Name("Subcommand")
@Description("Allows you to define a subcommand within a command. Works exactly like a command.")
public class SecSubcommand extends Section {

	static {
		Skript.registerSection(SecSubcommand.class, /*"subcommand from name %*string%", */"subcommand <.+>");
	}

	private StructCommand command;

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
