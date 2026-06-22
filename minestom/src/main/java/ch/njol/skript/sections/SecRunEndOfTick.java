package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

@Name("Run at End of Tick")
@Description({
	"Schedules the code inside this section to run at the end of the current server tick.",
	"Local variables from the current scope are preserved."
})
@Examples({
	"schedule to run at the end of the tick:",
	"    broadcast \"End of tick!\"",
	"run by end of tick:",
	"    delete {temp::*}"
})
public class SecRunEndOfTick extends Section {

	static {
		Skript.registerSection(SecRunEndOfTick.class, "[schedule to] run (by|at) [the] end of [the] tick");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		loadCode(sectionNode);
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		Object variables = Variables.copyLocalVariables(event);
		MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
			Variables.setLocalVariables(event, variables);
			TriggerItem.walk(this.first, event);
		});
		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "run at end of tick";
	}

}
