package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.ItemEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.item.ItemDropEvent;

public class ItemDropWrapper extends EventWrapper<ItemDropEvent> implements PlayerInstanceEventMarker, ItemEventMarker {

	public ItemDropWrapper(ItemDropEvent event) {
		super(event);
	}

}
