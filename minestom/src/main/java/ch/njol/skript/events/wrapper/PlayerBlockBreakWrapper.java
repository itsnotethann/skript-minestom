package ch.njol.skript.events.wrapper;

import ch.njol.skript.events.wrapper.marker.BlockEventMarker;
import ch.njol.skript.events.wrapper.marker.PlayerInstanceEventMarker;
import net.minestom.server.event.player.PlayerBlockBreakEvent;

public class PlayerBlockBreakWrapper extends EventWrapper<PlayerBlockBreakEvent> implements PlayerInstanceEventMarker, BlockEventMarker {

	public PlayerBlockBreakWrapper(PlayerBlockBreakEvent event) {
		super(event);
	}

}
