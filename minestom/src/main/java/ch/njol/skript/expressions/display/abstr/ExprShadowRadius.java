package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

public class ExprShadowRadius extends SimplePropertyExpression<Entity, Number> {

	static {
		register(ExprShadowRadius.class, Number.class, "[display] shadow radius", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Number convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return meta.getShadowRadius();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Number.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Number radius = delta == null ? null : (Number) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (radius == null) return;
					meta.setShadowRadius(radius.floatValue());
				}
				case RESET -> meta.setViewRange(0);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display shadow radius";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}
