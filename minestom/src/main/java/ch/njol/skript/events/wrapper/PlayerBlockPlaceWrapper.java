package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.BlockEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class PlayerBlockPlaceWrapper extends EventWrapper<PlayerBlockPlaceEvent> implements PlayerInstanceEventMarker, BlockEventMarker {

	public PlayerBlockPlaceWrapper(PlayerBlockPlaceEvent event) {
		super(event);
	}

}
