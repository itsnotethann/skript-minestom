package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.minestom.CustomConnectEvent;
import ch.njol.skript.events.wrapper.marker.PlayerEventMarker;

public class CustomConnectWrapper extends EventWrapper<CustomConnectEvent> implements PlayerEventMarker {

	public CustomConnectWrapper(CustomConnectEvent event) {
		super(event);
	}

}
