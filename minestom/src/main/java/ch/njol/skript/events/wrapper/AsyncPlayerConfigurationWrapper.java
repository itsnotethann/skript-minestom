package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerEventMarker;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class AsyncPlayerConfigurationWrapper extends EventWrapper<AsyncPlayerConfigurationEvent> implements PlayerEventMarker {

	public AsyncPlayerConfigurationWrapper(AsyncPlayerConfigurationEvent event) {
		super(event);
	}

}
