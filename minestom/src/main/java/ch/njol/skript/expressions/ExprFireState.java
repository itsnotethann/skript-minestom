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

@Name("Fire State")
@Description("The fire state of an entity. Unlike the fire time that goes down, this will persist until you turn it off.")
@Examples("set fire state of player to true")
public class ExprFireState extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprFireState.class, Boolean.class, "(fire|flaming) [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.isGlowing();
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
					entityMeta.setOnFire(state);
				}
				case RESET -> entityMeta.setOnFire(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "fire state";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
