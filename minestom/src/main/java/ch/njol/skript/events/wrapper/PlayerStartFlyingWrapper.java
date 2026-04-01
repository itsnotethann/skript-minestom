package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartFlyingEvent;

public class PlayerStartFlyingWrapper extends EventWrapper<PlayerStartFlyingEvent> implements PlayerInstanceEventMarker {

	public PlayerStartFlyingWrapper(PlayerStartFlyingEvent event) {
		super(event);
	}

}
