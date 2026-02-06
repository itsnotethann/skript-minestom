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

@Name("Automatic Viewability")
@Description("The automatic viewability state of an entity.")
@Examples("set auto viewable state of player to true")
public class ExprAutoViewable extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprAutoViewable.class, Boolean.class, "auto[matic] viewab(le|ility) [(state|property)]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.isAutoViewable();
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
					e.setAutoViewable(state);
				}
				case RESET -> e.setAutoViewable(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "auto viewable state";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}