package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.util.ComponentWrapper;
import net.minestom.server.event.player.PlayerChatEvent;
import org.skriptlang.skript.lang.converter.Converters;

public class PlayerChatWrapper extends EventWrapper<PlayerChatEvent> implements PlayerInstanceEventMarker {

	private final ComponentWrapper formattedPlayerMessage;

	public PlayerChatWrapper(PlayerChatEvent event) {
		super(event);
		formattedPlayerMessage = Converters.convert(event.getRawMessage(), ComponentWrapper.class);
	}

	public ComponentWrapper getFormattedPlayerMessage() {
		return formattedPlayerMessage;
	}

}
