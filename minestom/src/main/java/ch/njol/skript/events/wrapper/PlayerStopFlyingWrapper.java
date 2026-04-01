package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStopFlyingEvent;

public class PlayerStopFlyingWrapper extends EventWrapper<PlayerStopFlyingEvent> implements PlayerInstanceEventMarker {

	public PlayerStopFlyingWrapper(PlayerStopFlyingEvent event) {
		super(event);
	}

}
