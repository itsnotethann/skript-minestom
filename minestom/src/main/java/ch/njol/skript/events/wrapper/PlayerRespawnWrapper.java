package ch.njol.skript.events.wrapper;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerRespawnEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PlayerRespawnWrapper extends EventWrapper<PlayerRespawnEvent> implements PlayerInstanceEventMarker {

	static {
		EventValues.registerEventValue(EventValue.builder(PlayerRespawnWrapper.class, Pos.class)
			.patterns("respawn-(point|position)")
			.getter(from -> from.event.getRespawnPosition())
			.registerChanger(Changer.ChangeMode.SET, (event, value) -> event.event.setRespawnPosition(value))
			.registerChanger(Changer.ChangeMode.RESET, (event, value) -> {
				PlayerRespawnEvent e = event.event;
				e.setRespawnPosition(e.getPlayer().getRespawnPoint());
			})
			.build());
	}

	public PlayerRespawnWrapper(PlayerRespawnEvent event) {
		super(event);
	}

}
