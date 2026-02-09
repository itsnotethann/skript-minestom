package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.*;
import ch.njol.skript.lang.util.SimpleEvent;

public class SimpleEvents {

	static {
		Skript.registerEvent("Player Configuration/Connect", SimpleEvent.class, AsyncPlayerConfigurationWrapper.class, "[player] (config[ur(e|ation)]|connect[ing])")
			.description("Called when a player is configuring/connecting to the server.")
			.examples("on player connect:");
		//Skript.registerEvent("Player Spawn/Join Instance", SimpleEvent.class, PlayerSpawnWrapper.class, "[player] (spawn|join [instance])");
		Skript.registerEvent("Player Leave", SimpleEvent.class, PlayerDisconnectWrapper.class, "[player] (quit[ting]|disconnect[ing]|log[ ]out|logging out|leav(e|ing))")
			.description("Called when a player leaves the server.")
			.examples("on player quit:");
		Skript.registerEvent("Player Start Sneaking", SimpleEvent.class, PlayerStartSneakingWrapper.class, "[player] [start] sneak[ing]")
			.description("Called when a player starts sneaking.")
			.examples("on player start sneaking:");
		Skript.registerEvent("Player Stop Sneaking", SimpleEvent.class, PlayerStopSneakingWrapper.class, "[player] (stop |un)sneak[ing]")
			.description("Called when a player stops sneaking.")
			.examples("on player stop sneaking:");
		Skript.registerEvent("Player Chat", SimpleEvent.class, PlayerChatWrapper.class, "[player] chat")
			.description("Called when a player chats.")
			.examples("on chat:");
		Skript.registerEvent("Swap Hand Item", SimpleEvent.class, PlayerSwapItemWrapper.class, "swap[ping of] [(hand|held)] item[s]")
			.description("Called when a player swaps items in their hands.")
			.examples("on swap hand item:");
		Skript.registerEvent("Inventory Click", SimpleEvent.class, InventoryPreClickWrapper.class, "inventory click")
			.description("Called when a player clicks while in an inventory.")
			.examples("on inventory click:");
		Skript.registerEvent("Inventory Open", SimpleEvent.class, InventoryOpenWrapper.class, "inventory open")
			  .description("Called when a player opens an inventory.")
			  .examples("on inventory open:");
		Skript.registerEvent("Inventory Close", SimpleEvent.class, InventoryCloseWrapper.class, "inventory close")
			  .description("Called when a player closes an inventory.")
			  .examples("on inventory close:");
	}

}
