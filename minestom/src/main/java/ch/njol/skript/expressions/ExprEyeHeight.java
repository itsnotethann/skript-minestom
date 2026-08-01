package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jspecify.annotations.Nullable;

@Name("Eye Height")
@Description("The eye height of an entity/entity type.")
@Examples("set {_eye-height} to eye height of player")
public class ExprEyeHeight extends SimplePropertyExpression<Object, Number> {

	static {
		register(ExprEyeHeight.class, Number.class, "eye height", "entities/entitytypes");
	}

	@Override
	public @Nullable Number convert(Object from) {
		if (from instanceof EntityType entityType) return entityType.registry().eyeHeight();
		return ((Entity) from).getEyeHeight();
	}

	@Override
	protected String getPropertyName() {
		return "eye height";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}
