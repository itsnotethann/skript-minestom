package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerLoadedEvent;

public class PlayerLoadedWrapper extends EventWrapper<PlayerLoadedEvent> implements PlayerInstanceEventMarker {

	public PlayerLoadedWrapper(PlayerLoadedEvent event) {
		super(event);
	}

}
