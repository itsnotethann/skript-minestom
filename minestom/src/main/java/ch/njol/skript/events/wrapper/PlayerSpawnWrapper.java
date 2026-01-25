package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class PlayerSpawnWrapper extends EventWrapper<PlayerSpawnEvent> implements PlayerInstanceEventMarker {

	public PlayerSpawnWrapper(PlayerSpawnEvent event) {
		super(event);
	}

}
