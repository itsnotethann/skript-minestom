package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.PlayerStartFlyingWithElytraWrapper;
import ch.njol.skript.events.wrapper.PlayerStopFlyingWithElytraWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EvtToggleElytraFlight extends SkriptEvent {

	static {
		Class<? extends Event>[] eventTypes = CollectionUtils.array(PlayerStartFlyingWithElytraWrapper.class, PlayerStopFlyingWithElytraWrapper.class);
		Skript.registerEvent("Toggle Sneak", EvtToggleElytraFlight.class, eventTypes, "[player] elytra fl(ight|ying) toggle [1:on|2:off]")
			.description("Called when a player toggles their elytra flight state.")
			.examples("on elytra flight toggle:");
	}

	private boolean start = true;
	private boolean stop = true;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		if (parseResult.mark == 1) stop = false;
		if (parseResult.mark == 2) start = false;
		return true;
	}

	@Override
	public boolean check(Event event) {
		if (event instanceof PlayerStartFlyingWithElytraWrapper && start) return true;
		return event instanceof PlayerStopFlyingWithElytraWrapper && stop;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "player elytra flight toggle" + getRequiredState();
	}

	private String getRequiredState() {
		if (start && !stop) return " on";
		if (!start && stop) return " off";
		return "";
	}

}

