package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;


@Name("Display Scale")
@Description("The scale of a display entity.")
@Examples("set display scale of {_entity} to vector(2, 2, 2)")
public class ExprScale extends SimplePropertyExpression<Entity, Vec> {

	static {
		register(ExprScale.class, Vec.class, "[display] scale", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Vec convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return meta.getScale();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Vec.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Vec scale = delta == null ? null : (Vec) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (scale == null) return;
					meta.setScale(scale);
				}
				case RESET -> meta.setScale(Vec.ONE);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display scale";
	}

	@Override
	public Class<? extends Vec> getReturnType() {
		return Vec.class;
	}

}
