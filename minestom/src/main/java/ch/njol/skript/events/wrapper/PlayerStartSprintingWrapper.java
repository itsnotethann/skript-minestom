package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartSprintingEvent;

public class PlayerStartSprintingWrapper extends EventWrapper<PlayerStartSprintingEvent> implements PlayerInstanceEventMarker {

	public PlayerStartSprintingWrapper(PlayerStartSprintingEvent event) {
		super(event);
	}

}
