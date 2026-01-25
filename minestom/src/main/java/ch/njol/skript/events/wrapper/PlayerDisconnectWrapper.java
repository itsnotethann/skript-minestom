package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class PlayerDisconnectWrapper extends EventWrapper<PlayerDisconnectEvent> implements PlayerInstanceEventMarker {

	public PlayerDisconnectWrapper(PlayerDisconnectEvent event) {
		super(event);
	}

}
