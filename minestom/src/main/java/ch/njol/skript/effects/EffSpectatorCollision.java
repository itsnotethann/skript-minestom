package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

@Name("Spectator Collision")
@Description("Enables spectator collision mode for the given players, allowing them to collide with other players even in spectator mode.")
@Examples("enable spectator collision for all players")
public class EffSpectatorCollision extends Effect {

	static {
		Skript.registerEffect(EffSpectatorCollision.class, "enable spectator collision [mode] (on|for) %players%");
	}

	private Expression<Player> players;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		players = (Expression<Player>) expressions[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		Collection<Player> onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayers();
		for (Player player : players.getArray(event)) {
			ChangeGameStatePacket ability = new ChangeGameStatePacket(ChangeGameStatePacket.Reason.CHANGE_GAMEMODE, 3);
			player.sendPacket(ability);
			PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(player.getUuid(), player.getUsername(),
				Collections.emptyList(), true, 0, GameMode.SPECTATOR, player.getDisplayName(),
				null, 1, true);
			for (Player p : onlinePlayers) {
				if (p.equals(player)) continue;
				p.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, entry));
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "enable spectator collision mode for " + players.toString(event, debug);
	}

}
