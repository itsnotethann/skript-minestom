package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprPhysics extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprPhysics.class, Boolean.class, "physics [property]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.hasPhysics();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Boolean.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Boolean state = delta == null ? null : (Boolean) delta[0];
		for (Entity e : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (state == null) return;
					e.setHasPhysics(state);
				}
				case RESET -> e.setHasPhysics(true);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "physics property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}