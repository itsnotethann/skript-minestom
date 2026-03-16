package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.PlayerSpawnWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Join / First Player Spawn")
@Description("Called when a player joins the server for the first time in a session.")
@Examples("on player join:")
public class EvtFirstPlayerSpawn extends SkriptEvent {

	static {
		Skript.registerEvent("Join / First Player Spawn", EvtFirstPlayerSpawn.class, PlayerSpawnWrapper.class,
			"first player spawn",
			"[player] join");
	}

	@Override
	public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
		return true;
	}

	@Override
	public boolean check(@NotNull Event event) {
		return ((PlayerSpawnWrapper) event).getEvent().isFirstSpawn();
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "first player spawn";
	}

}
