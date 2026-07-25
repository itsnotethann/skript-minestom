package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.InventoryEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.ClickType;
import ch.njol.skript.util.Slot;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.Click;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

import java.util.List;

public class InventoryPreClickWrapper extends EventWrapper<InventoryPreClickEvent> implements PlayerInstanceEventMarker, InventoryEventMarker {

	static {
		EventValues.registerEventValue(InventoryPreClickWrapper.class, Slot.class, from -> {
			InventoryPreClickEvent event = from.event;
			return new Slot(event.getClickedItem(), event.getInventory(), event.getSlot());
		});
		EventValues.registerEventValue(InventoryPreClickWrapper.class, ClickType.class, from -> {
			InventoryPreClickEvent event = from.event;
			return ClickType.getType(event.getClick());
		});
		EventValues.registerEventValue(EventValue.builder(InventoryPreClickWrapper.class, Slot[].class)
			.patterns("drag-slots")
			.getter(from -> {
				InventoryPreClickEvent event = from.event;
				if (!(event.getClick() instanceof Click.Drag drag)) return new Slot[0];
				List<Integer> slots = drag.slots();
				Player player = event.getPlayer();
				AbstractInventory inventory = event.getInventory();
				int inventorySize = inventory.getSize();
				int dragAmount = slots.size();
				Slot[] dragSlots = new Slot[dragAmount];

				for (int i = 0; i < dragAmount; i++) {
					int slot = slots.get(i);
					AbstractInventory inv = inventory;
					int slotIndex = slot;
					if (slot >= inventorySize) {
						inv = player.getInventory();
						slotIndex -= inventorySize;
					}
					dragSlots[i] = new Slot(inv.getItemStack(slotIndex), inv, slotIndex);
				}
				return dragSlots;
			})
			.build());
		EventValues.registerEventValue(EventValue.builder(InventoryPreClickWrapper.class, Slot.class)
			.patterns("hotbar-slot")
			.getter(from -> {
				InventoryPreClickEvent event = from.event;
				if (!(event.getClick() instanceof Click.HotbarSwap swap)) return null;
				PlayerInventory playerInventory = event.getPlayer().getInventory();
				int slot = swap.hotbarSlot();
				return new Slot(playerInventory.getItemStack(slot), playerInventory, slot);
			})
			.build());
	}

	public InventoryPreClickWrapper(InventoryPreClickEvent event) {
		super(event);
	}

}
