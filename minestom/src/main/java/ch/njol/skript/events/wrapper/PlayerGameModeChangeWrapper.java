package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeWrapper extends EventWrapper<PlayerGameModeChangeEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(PlayerGameModeChangeWrapper.class, GameMode.class, from -> from.event.getNewGameMode());
	}

	public PlayerGameModeChangeWrapper(PlayerGameModeChangeEvent event) {
		super(event);
	}

}
