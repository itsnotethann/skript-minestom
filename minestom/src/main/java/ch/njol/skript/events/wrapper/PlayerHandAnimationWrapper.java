package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerHandAnimationEvent;

public class PlayerHandAnimationWrapper extends EventWrapper<PlayerHandAnimationEvent> implements PlayerInstanceEventMarker {

	public PlayerHandAnimationWrapper(PlayerHandAnimationEvent event) {
		super(event);
	}

}
