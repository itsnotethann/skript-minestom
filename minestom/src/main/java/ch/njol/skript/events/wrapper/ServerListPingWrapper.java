package ch.njol.skript.events.wrapper;

import net.minestom.server.event.server.ServerListPingEvent;

public class ServerListPingWrapper extends EventWrapper<ServerListPingEvent> {

	public ServerListPingWrapper(ServerListPingEvent event) {
		super(event);
	}

}
