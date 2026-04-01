package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerHandAnimationEvent;

public class PlayerHandAnimationWrapper extends EventWrapper<PlayerHandAnimationEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(PlayerHandAnimationWrapper.class, PlayerHand.class, from -> from.event.getHand());
	}

	public PlayerHandAnimationWrapper(PlayerHandAnimationEvent event) {
		super(event);
	}

}
