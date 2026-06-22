package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.structures.command.StructCommand;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.List;

@Name("Subcommand")
@Description({
	"Defines a subcommand within a command structure.",
	"The subcommand name and argument syntax are parsed from the section key.",
	"Supports the same entries as a command: aliases, condition, trigger, arguments, and nested subcommands."
})
@Examples({
	"command /admin:",
	"    trigger:",
	"        send \"Usage: /admin <subcommand>\"",
	"    subcommand reload:",
	"        trigger:",
	"            reload script \"admin.sk\"",
	"    subcommand kick <target: player>:",
	"        trigger:",
	"            kick {target}"
})
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
