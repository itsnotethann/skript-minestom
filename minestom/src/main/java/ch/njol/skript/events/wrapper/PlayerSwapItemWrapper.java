package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerSwapItemEvent;

public class PlayerSwapItemWrapper extends EventWrapper<PlayerSwapItemEvent> implements PlayerInstanceEventMarker {

	public PlayerSwapItemWrapper(PlayerSwapItemEvent event) {
		super(event);
	}

}
