package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartFlyingWithElytraEvent;

public class PlayerStartFlyingWithElytraWrapper extends EventWrapper<PlayerStartFlyingWithElytraEvent> implements PlayerInstanceEventMarker {

	public PlayerStartFlyingWithElytraWrapper(PlayerStartFlyingWithElytraEvent event) {
		super(event);
	}

}
