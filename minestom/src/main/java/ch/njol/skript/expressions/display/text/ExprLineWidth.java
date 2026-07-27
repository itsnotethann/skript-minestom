package ch.njol.skript.expressions.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.bukkit.event.Event;

@Name("Line Width")
@Description("The line width of a text display entity.")
@Examples("set line width of {_entity} to 200")
public class ExprLineWidth extends SimplePropertyExpression<Entity, Integer> {

	static {
		register(ExprLineWidth.class, Integer.class, "line width", "entities");
	}

	@Override
	public Integer convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return meta.getLineWidth();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Integer.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Integer width = delta == null ? null : (Integer) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (width == null) return;
					meta.setLineWidth(width);
				}
				case RESET -> meta.setLineWidth(200);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "line width";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}