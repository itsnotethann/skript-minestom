package ch.njol.skript.events.minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.server.common.DisconnectPacket;
import org.jspecify.annotations.NonNull;

public class CustomConnectEvent implements PlayerEvent {

	private final Player player;
	private boolean kicked = false;

	public CustomConnectEvent(Player player) {
		this.player = player;
	}

	public void kick(Component message) {
		player.sendPacket(new DisconnectPacket(message));
		MinecraftServer.getConnectionManager().removePlayer(player.getPlayerConnection());
		if (!player.isRemoved()) player.scheduleNextTick(Entity::remove);
		kicked = true;
	}

	public boolean isKicked() {
		return kicked;
	}

	public @NonNull Player getPlayer() {
		return player;
	}

}
