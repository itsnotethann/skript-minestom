package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.BlockEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockInteractEvent;

public class PlayerBlockInteractWrapper extends EventWrapper<PlayerBlockInteractEvent> implements PlayerInstanceEventMarker, BlockEventMarker {

	static {
		EventValues.registerEventValue(PlayerBlockInteractWrapper.class, BlockVec.class, from -> from.getEvent().getBlockPosition());
	}

	public PlayerBlockInteractWrapper(PlayerBlockInteractEvent event) {
		super(event);
	}

}
