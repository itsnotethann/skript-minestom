package ch.njol.skript.events.wrapper;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.event.player.PlayerCommandEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerCommandWrapper extends EventWrapper<PlayerCommandEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerCommandWrapper.class, String.class)
			.patterns("command")
			.getter(from -> from.event.getCommand())
			.registerChanger(Changer.ChangeMode.SET, (event, value) -> event.event.setCommand(value))
			.build());
	}

	public PlayerCommandWrapper(PlayerCommandEvent event) {
		super(event);
	}

}
