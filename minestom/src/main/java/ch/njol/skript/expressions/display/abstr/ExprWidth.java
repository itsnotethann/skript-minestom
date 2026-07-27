package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import org.bukkit.event.Event;


@Name("Width")
@Description("The width of a display or interaction entity.")
@Examples("set width of {_entity} to 1")
public class ExprWidth extends SimplePropertyExpression<Entity, Number> {

	static {
		register(ExprWidth.class, Number.class, "width", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Number convert(Entity from) {
		EntityMeta entityMeta = from.getEntityMeta();
		if (entityMeta instanceof AbstractDisplayMeta meta) return meta.getWidth();
		if (entityMeta instanceof InteractionMeta meta) return meta.getWidth();
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
		Number width = delta == null ? null : (Number) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			EntityMeta entityMeta = entity.getEntityMeta();
			if (entityMeta instanceof AbstractDisplayMeta meta) {
				switch (mode) {
					case SET -> {
						if (width == null) return;
						meta.setWidth(width.floatValue());
					}
					case RESET -> meta.setWidth(0);
				}
			} else if (entityMeta instanceof InteractionMeta meta) {
				switch (mode) {
					case SET -> {
						if (width == null) return;
						meta.setWidth(width.floatValue());
					}
					case RESET -> meta.setWidth(0);
				}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "width";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}
