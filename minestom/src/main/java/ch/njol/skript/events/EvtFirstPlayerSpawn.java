package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.EventWrapper;
import ch.njol.skript.events.wrapper.PlayerSpawnWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

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

	@SuppressWarnings("unchecked")
	@Override
	public boolean check(@NotNull Event event) {
		return ((EventWrapper<PlayerSpawnEvent>) event).getEvent().isFirstSpawn();
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "first player spawn";
	}

}
