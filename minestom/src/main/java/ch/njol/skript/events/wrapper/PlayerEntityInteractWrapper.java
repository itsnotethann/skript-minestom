package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerEntityInteractEvent;

public class PlayerEntityInteractWrapper extends EventWrapper<PlayerEntityInteractEvent> implements PlayerInstanceEventMarker {

	public PlayerEntityInteractWrapper(PlayerEntityInteractEvent event) {
		super(event);
	}

}
