package com.github.hapily04.skriptminestom;

import net.minestom.server.event.Event;
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

}
