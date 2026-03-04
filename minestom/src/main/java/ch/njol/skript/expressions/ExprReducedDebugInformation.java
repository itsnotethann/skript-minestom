package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprReducedDebugInformation extends SimplePropertyExpression<Player, Boolean> {

	static {
		register(ExprReducedDebugInformation.class, Boolean.class, "reduced debug info[rmation] [property]", "players");
	}

	@Override
	public @Nullable Boolean convert(Player from) {
		return from.hasReducedDebugScreenInformation();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Boolean.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Boolean state = delta == null ? null : (Boolean) delta[0];
		for (Player p : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (state == null) return;
					p.setReducedDebugScreenInformation(state);
				}
				case RESET -> p.setReducedDebugScreenInformation(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "reduced debug information property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}