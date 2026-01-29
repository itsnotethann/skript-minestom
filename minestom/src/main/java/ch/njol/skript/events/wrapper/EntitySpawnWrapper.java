package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.EntityInstanceEventMarker;
import net.minestom.server.event.entity.EntitySpawnEvent;

public class EntitySpawnWrapper extends EventWrapper<EntitySpawnEvent> implements EntityInstanceEventMarker {

	public EntitySpawnWrapper(EntitySpawnEvent event) {
		super(event);
	}

}
