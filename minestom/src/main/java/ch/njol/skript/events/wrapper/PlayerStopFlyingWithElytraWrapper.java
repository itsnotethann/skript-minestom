package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerStartFlyingWithElytraEvent;
import net.minestom.server.event.player.PlayerStopFlyingWithElytraEvent;

public class PlayerStopFlyingWithElytraWrapper extends EventWrapper<PlayerStopFlyingWithElytraEvent> implements PlayerInstanceEventMarker {

	public PlayerStopFlyingWithElytraWrapper(PlayerStopFlyingWithElytraEvent event) {
		super(event);
	}

}
