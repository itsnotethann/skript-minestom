package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.BlockEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerPickBlockWrapper extends EventWrapper<PlayerPickBlockEvent> implements PlayerInstanceEventMarker, BlockEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerPickBlockWrapper.class, Boolean.class)
			.patterns("includes-data")
			.getter(from -> from.event.isIncludeData())
			.build());
	}

	public PlayerPickBlockWrapper(PlayerPickBlockEvent event) {
		super(event);
	}

}

