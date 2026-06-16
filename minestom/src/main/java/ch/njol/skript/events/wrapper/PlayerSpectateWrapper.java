package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerSpectateEntityEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerSpectateWrapper extends EventWrapper<PlayerSpectateEntityEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerSpectateWrapper.class, Entity.class)
			.patterns("target")
			.getter(from -> from.event.getTarget())
			.build());
	}

	public PlayerSpectateWrapper(PlayerSpectateEntityEvent event) {
		super(event);
	}

}
