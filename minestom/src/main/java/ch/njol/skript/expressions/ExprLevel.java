package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Level")
@Description("The experience level of a player.")
@Example("reduce the event-victim's level by 1")
@Example("set the player's level to 0")
public class ExprLevel extends SimplePropertyExpression<Player, Integer> {

	static {
		registerDefault(ExprLevel.class, Integer.class, "[xp|exp[erience]] level", "players");
	}

	@Override
	public @Nullable Integer convert(Player player) {
		return player.getLevel();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return new Class[] {Number.class};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert mode != ChangeMode.REMOVE_ALL;
		int deltaAmount = delta == null ? 0 : ((Number) delta[0]).intValue();

		for (Player player : getExpr().getArray(event)) {
			int level = player.getLevel();
			switch (mode) {
				case SET:
					level = deltaAmount;
					break;
				case ADD:
					level += deltaAmount;
					break;
				case REMOVE:
					level -= deltaAmount;
					break;
				case DELETE:
				case RESET:
					level = 0;
					break;
			}
			if (level < 0)
				level = 0;
			player.setLevel(level);
		}
	}

	@Override
	public Class<Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "level";
	}

}
