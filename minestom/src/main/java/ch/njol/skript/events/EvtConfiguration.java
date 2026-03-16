package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.AsyncPlayerConfigurationWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class EvtConfiguration extends SkriptEvent {

	static {
		Skript.registerEvent("Configuration / First Configuration", EvtConfiguration.class, AsyncPlayerConfigurationWrapper.class,
			"[:first] [player] config[ur(e|ation)]");
	}

	private boolean first;

	@Override
	public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
		first = parseResult.hasTag("first");
		return true;
	}

	@Override
	public boolean check(@NotNull Event event) {
		AsyncPlayerConfigurationEvent e = ((AsyncPlayerConfigurationWrapper) event).getEvent();
		if (first) return e.isFirstConfig();
		return true;
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return (first ? "first " : "") + "player configuration";
	}

}

