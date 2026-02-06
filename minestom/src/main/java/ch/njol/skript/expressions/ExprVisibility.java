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

@Name("Visibility")
@Description("The visibility state of an entity.")
@Examples("set visibility of player to false")
public class ExprVisibility extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprVisibility.class, Boolean.class, "visibility [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return !from.isInvisible();
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
					e.setInvisible(!state);
				}
				case RESET -> e.setInvisible(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "visibility";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
