package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class InventoryPreClickWrapper extends EventWrapper<InventoryPreClickEvent> implements PlayerInstanceEventMarker, InventoryEventMarker {

	public InventoryPreClickWrapper(InventoryPreClickEvent event) {
		super(event);
	}

}
