package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class EntityAttackWrapper extends EventWrapper<EntityAttackEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(EntityAttackWrapper.class, Entity.class)
			.patterns("attacker")
			.getter(from -> from.getEvent().getEntity())
			.build());
		EventValues.registerEventValue(EventValue.builder(EntityAttackWrapper.class, Entity.class)
			.patterns("victim")
			.getter(from -> from.getEvent().getTarget())
			.build());
	}

	public EntityAttackWrapper(EntityAttackEvent event) {
		super(event);
	}

}
