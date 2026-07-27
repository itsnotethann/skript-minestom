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
import org.jspecify.annotations.Nullable;


@Name("Use Default Background")
@Description("Whether a text display entity uses the default background color.")
@Examples("set use default background property of {_entity} to true")
public class ExprUseDefaultBackground extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprUseDefaultBackground.class, Boolean.class, "[use] default background [color] [(property|state)]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return meta.isUseDefaultBackground();
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
			if (!(e.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (state == null) return;
					meta.setUseDefaultBackground(state);
				}
				case RESET -> meta.setUseDefaultBackground(true);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "use default background property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
