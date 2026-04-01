package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Slot;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;

public class PlayerChangeHeldSlotWrapper extends EventWrapper<PlayerChangeHeldSlotEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(PlayerChangeHeldSlotWrapper.class, Slot.class, from -> {
			PlayerChangeHeldSlotEvent event = from.event;
			return new Slot(event.getItemInOldSlot(), event.getPlayer().getInventory(), event.getOldSlot());
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PlayerChangeHeldSlotWrapper.class, Slot.class, from -> {
			PlayerChangeHeldSlotEvent event = from.event;
			return new Slot(event.getItemInNewSlot(), event.getPlayer().getInventory(), event.getNewSlot());
		}, EventValues.TIME_FUTURE);
	}

	public PlayerChangeHeldSlotWrapper(PlayerChangeHeldSlotEvent event) {
		super(event);
	}

}
