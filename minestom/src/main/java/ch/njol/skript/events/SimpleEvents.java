package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.*;
import ch.njol.skript.lang.util.SimpleEvent;

public class SimpleEvents {

	static {
		Skript.registerEvent("Player Configuration/Connect", SimpleEvent.class, AsyncPlayerConfigurationWrapper.class, "[player] (config[ur(e|ation)]|connect[ing])");
		Skript.registerEvent("Player Spawn/Join Instance", SimpleEvent.class, PlayerSpawnWrapper.class, "[player] (spawn|join [instance])");
		Skript.registerEvent("Player Leave", SimpleEvent.class, PlayerDisconnectWrapper.class, "[player] (quit[ting]|disconnect[ing]|log[ ]out|logging out|leav(e|ing))");
		Skript.registerEvent("Player Start Sneaking", SimpleEvent.class, PlayerStartSneakingWrapper.class, "[player] [start] sneak[ing]");
		Skript.registerEvent("Player Stop Sneaking", SimpleEvent.class, PlayerStopSneakingWrapper.class, "[player] (stop |un)sneak[ing]");
		Skript.registerEvent("Player Chat", SimpleEvent.class, PlayerChatWrapper.class, "[player] chat");
	}

}
