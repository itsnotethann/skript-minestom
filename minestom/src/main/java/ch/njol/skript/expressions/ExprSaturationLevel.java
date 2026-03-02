package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprSaturationLevel extends SimplePropertyExpression<Player, Number> {

	static {
		register(ExprSaturationLevel.class, Number.class, "saturation [level]", "players");
	}

	@Override
	public @Nullable Number convert(Player from) {
		return from.getFoodSaturation();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Player[] players = getExpr().getArray(event);
		Number saturationLevel = delta == null ? null : (Number) delta[0];
		for (Player player : players) {
			if (mode == Changer.ChangeMode.RESET) {
				player.setFoodSaturation(0);
				continue;
			}
			if (saturationLevel == null) return;
			switch (mode) {
				case ADD -> player.setFoodSaturation(Math.clamp(player.getFoodSaturation()+saturationLevel.floatValue(), 0, 20));
				case REMOVE -> player.setFoodSaturation(Math.clamp(player.getFoodSaturation()-saturationLevel.floatValue(), 0, 20));
				case SET -> player.setFoodSaturation(Math.clamp(saturationLevel.floatValue(), 0, 20));
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "saturation level";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}

