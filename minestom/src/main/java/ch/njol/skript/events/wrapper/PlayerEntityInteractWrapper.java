package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerEntityInteractWrapper extends EventWrapper<PlayerEntityInteractEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerEntityInteractWrapper.class, Entity.class)
			.patterns("attacker")
			.getter(from -> from.getEvent().getPlayer())
			.build());
		EventValues.registerEventValue(EventValue.builder(PlayerEntityInteractWrapper.class, Entity.class)
			.patterns("victim")
			.getter(from -> from.getEvent().getTarget())
			.build());
	}

	public PlayerEntityInteractWrapper(PlayerEntityInteractEvent event) {
		super(event);
	}

}
