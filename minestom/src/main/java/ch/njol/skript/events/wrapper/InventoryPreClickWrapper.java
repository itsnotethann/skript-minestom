package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Slot;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class InventoryPreClickWrapper extends EventWrapper<InventoryPreClickEvent> implements PlayerInstanceEventMarker, InventoryEventMarker {

	static {
		EventValues.registerEventValue(InventoryPreClickWrapper.class, Slot.class, from -> {
			InventoryPreClickEvent event = from.event;
			return new Slot(event.getClickedItem(), event.getInventory(), event.getSlot());
		});
	}

	public InventoryPreClickWrapper(InventoryPreClickEvent event) {
		super(event);
	}

}
