package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

public class ExprShadowStrength extends SimplePropertyExpression<Entity, Number> {

	static {
		register(ExprShadowStrength.class, Number.class, "[display] shadow strength", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Number convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return meta.getShadowStrength();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Number.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Number strength = delta == null ? null : (Number) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (strength == null) return;
					meta.setShadowStrength(strength.floatValue());
				}
				case RESET -> meta.setShadowStrength(1);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display shadow strength";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}
