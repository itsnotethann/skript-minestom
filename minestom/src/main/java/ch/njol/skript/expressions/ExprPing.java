package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Player;
import org.jspecify.annotations.Nullable;

public class ExprPing extends SimplePropertyExpression<Player, Integer> {

	static {
		register(ExprPing.class, Integer.class, "(ping|latency)", "players");
	}

	@Override
	public @Nullable Integer convert(Player from) {
		return from.getLatency();
	}

	@Override
	protected String getPropertyName() {
		return "ping";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
