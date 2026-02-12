package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.ItemEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.item.PlayerBeginItemUseEvent;

public class PlayerBeginItemUseWrapper extends EventWrapper<PlayerBeginItemUseEvent> implements PlayerInstanceEventMarker, ItemEventMarker {

	public PlayerBeginItemUseWrapper(PlayerBeginItemUseEvent event) {
		super(event);
	}

}
