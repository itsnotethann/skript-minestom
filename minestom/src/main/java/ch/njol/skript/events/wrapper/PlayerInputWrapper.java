package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.InputKey;
import net.minestom.server.event.player.PlayerInputEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerInputWrapper extends EventWrapper<PlayerInputEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.simple(PlayerInputWrapper.class, InputKey[].class,
			from -> InputKey.fromInput(from.event.getPlayer().inputs()).toArray(new InputKey[0])));
		EventValues.registerEventValue(EventValue.builder(PlayerInputWrapper.class, InputKey[].class)
			.time(EventValue.Time.PAST)
			.getter(from -> InputKey.fromInput(from.event).toArray(new InputKey[0]))
			.build());
	}

	public PlayerInputWrapper(PlayerInputEvent event) {
		super(event);
	}

}
