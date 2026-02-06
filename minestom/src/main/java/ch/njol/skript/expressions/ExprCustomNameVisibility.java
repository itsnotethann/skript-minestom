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

@Name("Custom Name Visibility")
@Description("The custom name visibility state of an entity.")
@Examples("set custom name visibility of player to true")
public class ExprCustomNameVisibility extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprCustomNameVisibility.class, Boolean.class, "custom name visibil(e|ity) [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.isCustomNameVisible();
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
					e.setCustomNameVisible(state);
				}
				case RESET -> e.setCustomNameVisible(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "custom name visibility";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
