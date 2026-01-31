package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

public class ExprTranslation extends SimplePropertyExpression<Entity, Point> {

	static {
		register(ExprTranslation.class, Point.class,
			"[display] translation", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Point convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return meta.getTranslation();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Point.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Point translation = delta == null ? null : (Point) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (translation == null) return;
					meta.setTranslation(translation);
				}
				case RESET -> meta.setTranslation(Vec.ZERO);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display translation";
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

}