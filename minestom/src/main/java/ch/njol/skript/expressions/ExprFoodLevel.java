package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprFoodLevel extends SimplePropertyExpression<Player, Integer> {

	static {
		register(ExprFoodLevel.class, Integer.class, "(food|hunger)[[ ](level|met(er|re)|bar)]", "players");
	}

	@Override
	public @Nullable Integer convert(Player from) {
		return from.getFood();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Player[] players = getExpr().getArray(event);
		Integer foodLevel = delta == null ? null : (Integer) delta[0];
		for (Player player : players) {
			if (mode == Changer.ChangeMode.RESET) {
				player.setFood(20);
				continue;
			}
			if (foodLevel == null) return;
			switch (mode) {
				case ADD -> player.setFood(Math.clamp(player.getFood()+foodLevel, 0, 20));
				case REMOVE -> player.setFood(Math.clamp(player.getFood()-foodLevel, 0, 20));
				case SET -> player.setFood(Math.clamp(foodLevel, 0, 20));
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "food level";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
