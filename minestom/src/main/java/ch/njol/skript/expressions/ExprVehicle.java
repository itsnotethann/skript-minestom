package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

@Name("Vehicle")
@Description("The vehicle that an entity is riding.")
@Examples("set {_v} to vehicle of player")
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
