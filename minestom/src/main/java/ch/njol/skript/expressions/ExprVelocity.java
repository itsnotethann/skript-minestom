package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

// skript velocity values are 1/20th (20 ticks per second) the value of skript's because minestom works in blocks per tick instead of blocks per second
// if you want the same push upwards you need to do vector(0, 20, 0), not vector(0, 1, 0)

@Name("Velocity")
@Description("The velocity of an entity.")
@Examples("set velocity of player to vector(0, 1, 0)")
public class ExprVelocity extends SimplePropertyExpression<Entity, Vec> {

	static {
		register(ExprVelocity.class, Vec.class, "velocity", "entities");
	}

	@Override
	public @Nullable Vec convert(Entity from) {
		return from.getVelocity();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case REMOVE, ADD, SET, RESET, DELETE -> CollectionUtils.array(Vec.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Vec vector = delta == null ? null : (Vec) delta[0];
		for (Entity e : getExpr().getArray(event)) {
			if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
				e.setVelocity(Vec.ZERO);
				continue;
			}
			if (vector == null) return;
			switch (mode) {
				case SET -> e.setVelocity(vector);
				case ADD -> e.setVelocity(e.getVelocity().add(vector));
				case REMOVE -> e.setVelocity(e.getVelocity().sub(vector));
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "velocity";
	}

	@Override
	public Class<? extends Vec> getReturnType() {
		return Vec.class;
	}

}
