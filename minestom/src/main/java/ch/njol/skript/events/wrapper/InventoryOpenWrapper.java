package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.inventory.InventoryOpenEvent;

public class InventoryOpenWrapper extends EventWrapper<InventoryOpenEvent> implements PlayerInstanceEventMarker, InventoryEventMarker {

	public InventoryOpenWrapper(InventoryOpenEvent event) {
		super(event);
	}

}
