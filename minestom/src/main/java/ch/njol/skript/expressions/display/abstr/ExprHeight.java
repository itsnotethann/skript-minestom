package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import org.bukkit.event.Event;

public class ExprHeight extends SimplePropertyExpression<Entity, Number> {

	static {
		register(ExprHeight.class, Number.class, "width", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Number convert(Entity from) {
		EntityMeta entityMeta = from.getEntityMeta();
		if (entityMeta instanceof AbstractDisplayMeta meta) return meta.getHeight();
		if (entityMeta instanceof InteractionMeta meta) return meta.getHeight();
		return null;
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Number.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Number height = delta == null ? null : (Number) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			EntityMeta entityMeta = entity.getEntityMeta();
			if (entityMeta instanceof AbstractDisplayMeta meta) {
				switch (mode) {
					case SET -> {
						if (height == null) return;
						meta.setHeight(height.floatValue());
					}
					case RESET -> meta.setHeight(0);
				}
			} else if (entityMeta instanceof InteractionMeta meta) {
				switch (mode) {
					case SET -> {
						if (height == null) return;
						meta.setHeight(height.floatValue());
					}
					case RESET -> meta.setHeight(0);
				}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "height";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}