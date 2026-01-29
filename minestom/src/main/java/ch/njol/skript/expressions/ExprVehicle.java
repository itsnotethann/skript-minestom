package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ExprVehicle extends SimplePropertyExpression<Entity, Entity> {

	static {
		register(ExprVehicle.class, Entity.class, "vehicle", "entities");
	}

	@Override
	public @Nullable Entity convert(Entity from) {
		return from.getVehicle();
	}

	@Override
	protected String getPropertyName() {
		return "vehicle";
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

}
