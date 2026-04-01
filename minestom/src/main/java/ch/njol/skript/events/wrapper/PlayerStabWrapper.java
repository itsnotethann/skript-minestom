package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Slot;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.event.player.PlayerStabEvent;

public class PlayerStabWrapper extends EventWrapper<PlayerStabEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(PlayerStabWrapper.class, Slot.class, from -> {
			PlayerStabEvent event = from.event;
			return new Slot(event.getItemStack(), event.getPlayer(), EquipmentSlot.MAIN_HAND);
		});
	}

	public PlayerStabWrapper(PlayerStabEvent event) {
		super(event);
	}

}
