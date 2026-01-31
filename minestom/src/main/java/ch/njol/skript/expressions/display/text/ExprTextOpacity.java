package ch.njol.skript.expressions.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.bukkit.event.Event;

public class ExprTextOpacity extends SimplePropertyExpression<Entity, Integer> {

	static {
		register(ExprTextOpacity.class, Integer.class, "text opacity", "entities");
	}

	@Override
	public Integer convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return (int) meta.getTextOpacity();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Integer.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Integer opacity = delta == null ? null : (Integer) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (opacity == null) return;
					meta.setTextOpacity(opacity.byteValue());
				}
				case RESET -> meta.setTextOpacity((byte) -1);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "text opacity";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}