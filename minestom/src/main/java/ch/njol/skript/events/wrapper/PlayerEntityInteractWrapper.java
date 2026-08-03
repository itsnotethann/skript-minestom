package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Slot;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerEntityInteractWrapper extends EventWrapper<PlayerEntityInteractEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.simple(PlayerEntityInteractWrapper.class, Slot.class, from -> {
			PlayerEntityInteractEvent e = from.event;
			Player player = e.getPlayer();
			PlayerHand hand = e.getHand();
			return new Slot(player.getItemInHand(hand), player, hand == PlayerHand.MAIN ? EquipmentSlot.MAIN_HAND : EquipmentSlot.OFF_HAND);
		}));
		EventValues.registerEventValue(EventValue.builder(PlayerEntityInteractWrapper.class, Entity.class)
			.patterns("clicked-entity")
			.getter(from -> from.getEvent().getTarget())
			.build());
	}

	public PlayerEntityInteractWrapper(PlayerEntityInteractEvent event) {
		super(event);
	}

}
