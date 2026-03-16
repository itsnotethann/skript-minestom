package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.inventory.InventoryCloseEvent;

public class InventoryCloseWrapper extends EventWrapper<InventoryCloseEvent> implements PlayerInstanceEventMarker, InventoryEventMarker {

	public InventoryCloseWrapper(InventoryCloseEvent event) {
		super(event);
	}

}
