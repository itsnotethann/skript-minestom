package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.BlockEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartDiggingEvent;

public class PlayerStartDiggingWrapper extends EventWrapper<PlayerStartDiggingEvent> implements PlayerInstanceEventMarker, BlockEventMarker {

	public PlayerStartDiggingWrapper(PlayerStartDiggingEvent event) {
		super(event);
	}

}
