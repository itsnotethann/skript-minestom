package ch.njol.skript.events.wrapper;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerMoveWrapper extends EventWrapper<PlayerMoveEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerMoveWrapper.class, Boolean.class)
			.patterns("grounded")
			.getter(from -> from.event.isOnGround())
			.time(EventValue.Time.FUTURE)
			.build());
		EventValues.registerEventValue(EventValue.builder(PlayerMoveWrapper.class, Boolean.class)
			.patterns("grounded")
			.getter(from -> from.event.getPlayer().isOnGround())
			.build());
		EventValues.registerEventValue(EventValue.builder(PlayerMoveWrapper.class, Pos.class)
			.getter(from -> from.event.getPlayer().getPosition())
			.build());
		EventValues.registerEventValue(EventValue.builder(PlayerMoveWrapper.class, Pos.class)
			.getter(from -> from.event.getNewPosition())
			.time(EventValue.Time.FUTURE)
			.registerChanger(Changer.ChangeMode.SET, (event, value) -> event.event.setNewPosition(value))
			.build());
	}

	public PlayerMoveWrapper(PlayerMoveEvent event) {
		super(event);
	}

}
