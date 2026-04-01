package ch.njol.skript.events.wrapper;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerPickEntityEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerPickEntityWrapper extends EventWrapper<PlayerPickEntityEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerPickEntityWrapper.class, Entity.class)
			.patterns("target")
			.getter(from -> from.event.getTarget())
			.build());
		EventValues.registerEventValue(EventValue.builder(PlayerPickEntityWrapper.class, Boolean.class)
			.patterns("data-inclusion")
			.getter(from -> from.event.isIncludeData())
			.build());
	}

	public PlayerPickEntityWrapper(PlayerPickEntityEvent event) {
		super(event);
	}

}
