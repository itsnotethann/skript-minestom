package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ExprPreviousPosition extends SimplePropertyExpression<Entity, Pos> {

	static {
		register(ExprPreviousPosition.class, Pos.class, "previous position", "entities");
	}

	@Override
	public @Nullable Pos convert(Entity from) {
		return from.getPreviousPosition();
	}

	@Override
	protected String getPropertyName() {
		return "previous position";
	}

	@Override
	public Class<? extends Pos> getReturnType() {
		return Pos.class;
	}

}
