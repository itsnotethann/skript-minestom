package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStopSprintingEvent;

public class PlayerStopSprintingWrapper extends EventWrapper<PlayerStopSprintingEvent> implements PlayerInstanceEventMarker {

	public PlayerStopSprintingWrapper(PlayerStopSprintingEvent event) {
		super(event);
	}

}