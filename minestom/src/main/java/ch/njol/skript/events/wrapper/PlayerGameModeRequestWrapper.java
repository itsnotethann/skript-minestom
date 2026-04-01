package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerGameModeRequestEvent;

public class PlayerGameModeRequestWrapper extends EventWrapper<PlayerGameModeRequestEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(PlayerGameModeRequestWrapper.class, GameMode.class, from -> from.event.getRequestedGameMode());
	}

	public PlayerGameModeRequestWrapper(PlayerGameModeRequestEvent event) {
		super(event);
	}

}
