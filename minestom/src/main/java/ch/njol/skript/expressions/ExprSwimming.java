package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.EntityMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Swimming")
@Description("Whether an entity is swimming.")
@Examples("set swimming state of player to false")
public class ExprSwimming extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprSwimming.class, Boolean.class, "swim[ing] [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.getEntityMeta().isSwimming();
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
			EntityMeta entityMeta = e.getEntityMeta();
			switch (mode) {
				case SET -> {
					if (state == null) return;
					entityMeta.setSwimming(state);
				}
				case RESET -> entityMeta.setSwimming(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "swimming";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
