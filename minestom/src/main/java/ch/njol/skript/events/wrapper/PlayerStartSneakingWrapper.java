package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartSneakingEvent;

public class PlayerStartSneakingWrapper extends EventWrapper<PlayerStartSneakingEvent> implements PlayerInstanceEventMarker {

	public PlayerStartSneakingWrapper(PlayerStartSneakingEvent event) {
		super(event);
	}

}