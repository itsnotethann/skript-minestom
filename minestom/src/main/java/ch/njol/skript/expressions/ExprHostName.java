package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jspecify.annotations.Nullable;

public class ExprHostName extends SimplePropertyExpression<Player, String> {

	static {
		register(ExprHostName.class, String.class, "host[ ]name", "players");
	}

	@Override
	public @Nullable String convert(Player from) {
		PlayerConnection playerConnection = from.getPlayerConnection();
		return playerConnection.getServerAddress() + ":" + playerConnection.getServerPort();
	}

	@Override
	protected String getPropertyName() {
		return "host name";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
