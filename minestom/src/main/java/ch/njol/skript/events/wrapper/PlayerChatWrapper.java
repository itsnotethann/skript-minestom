package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerChatEvent;

public class PlayerChatWrapper extends EventWrapper<PlayerChatEvent> implements PlayerInstanceEventMarker {

	public PlayerChatWrapper(PlayerChatEvent event) {
		super(event);
	}

}
