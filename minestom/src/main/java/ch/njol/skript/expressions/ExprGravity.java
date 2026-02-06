package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Gravity")
@Description("The gravity state of an entity.")
@Examples("set gravity of player to false")
public class ExprGravity extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprGravity.class, Boolean.class, "gravity [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return !from.hasNoGravity();
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
		for (Entity e : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (state == null) return;
					e.setNoGravity(!state);
				}
				case RESET -> e.setNoGravity(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "gravity";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
