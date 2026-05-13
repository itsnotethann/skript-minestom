package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class CondIsStopping extends Condition {

	static {
		Skript.registerCondition(CondIsStopping.class,
			"server is stopping",
			"server (is not|isn't) stopping");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event event) {
		boolean stopping = MinecraftServer.isStopping();
		return isNegated() ? !stopping : stopping;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "server is" + (isNegated() ? "n't" : "") + " stopping";
	}

}
