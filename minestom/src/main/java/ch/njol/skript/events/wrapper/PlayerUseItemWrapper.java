package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.ItemEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerUseItemEvent;

public class PlayerUseItemWrapper extends EventWrapper<PlayerUseItemEvent> implements PlayerInstanceEventMarker, ItemEventMarker {

	public PlayerUseItemWrapper(PlayerUseItemEvent event) {
		super(event);
	}

}
