package com.github.hapily04.skriptminestom;

import ch.njol.skript.events.minestom.CustomConnectEvent;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;

import java.net.URISyntaxException;

public final class SkriptMinestomBootstrap {

	private SkriptMinestomBootstrap() {
	}

	public static void boot(EventNode<Event> node) throws URISyntaxException {
		SkriptMinestom.initSkript(node);
	}

	public static void bootEffectCommands(EventNode<Event> node) {
		SkriptMinestom.initEffectCommands(node);
	}

	public static boolean fireConnectEvent(Player player) {
		CustomConnectEvent connectEvent = new CustomConnectEvent(player);
		EventDispatcher.call(connectEvent);
		return !connectEvent.isKicked();
	}

}
