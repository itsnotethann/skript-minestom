package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Slot;
import net.minestom.server.event.inventory.InventoryBundleItemSelectEvent;
import net.minestom.server.inventory.AbstractInventory;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class InventoryBundleItemSelectWrapper extends EventWrapper<InventoryBundleItemSelectEvent> implements InventoryEventMarker, PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(InventoryBundleItemSelectWrapper.class, Integer.class)
			.patterns("selected-index")
			.getter(from -> from.event.getSelectedItemIndex())
			.build());
		EventValues.registerEventValue(EventValue.simple(InventoryBundleItemSelectWrapper.class, Slot.class, from -> {
			InventoryBundleItemSelectEvent event = from.event;
			AbstractInventory inventory = event.getInventory();
			int slot = event.getSlot();
			return new Slot(inventory.getItemStack(slot), inventory, slot);
		}));
	}

	public InventoryBundleItemSelectWrapper(InventoryBundleItemSelectEvent event) {
		super(event);
	}

}
