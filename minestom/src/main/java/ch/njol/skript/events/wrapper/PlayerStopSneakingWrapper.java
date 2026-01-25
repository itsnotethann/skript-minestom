package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStopSneakingEvent;

public class PlayerStopSneakingWrapper extends EventWrapper<PlayerStopSneakingEvent> implements PlayerInstanceEventMarker {

	public PlayerStopSneakingWrapper(PlayerStopSneakingEvent event) {
		super(event);
	}

}
