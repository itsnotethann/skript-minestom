package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.entity.EntityAttackEvent;

public class EntityAttackWrapper extends EventWrapper<EntityAttackEvent> implements PlayerInstanceEventMarker {

	public EntityAttackWrapper(EntityAttackEvent event) {
		super(event);
	}

}
