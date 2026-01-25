package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.*;
import ch.njol.skript.lang.util.SimpleEvent;

public class SimpleEvents {

	static {
		Skript.registerEvent("Player Configuration", SimpleEvent.class, AsyncPlayerConfigurationWrapper.class, "[player] config[ur(e|ation)]");
		Skript.registerEvent("Player Spawn", SimpleEvent.class, PlayerSpawnWrapper.class, "[player] spawn");
		Skript.registerEvent("Player Leave", SimpleEvent.class, PlayerDisconnectWrapper.class, "[player] (quit[ting]|disconnect[ing]|log[ ]out|logging out|leav(e|ing))");
		Skript.registerEvent("Player Start Sneaking", SimpleEvent.class, PlayerStartSneakingWrapper.class, "[player] [start] sneak[ing]");
		Skript.registerEvent("Player Stop Sneaking", SimpleEvent.class, PlayerStopSneakingWrapper.class, "[player] (stop |un[-])sneak[ing]");
	}

}
