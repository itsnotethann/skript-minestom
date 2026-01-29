package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ExprUUID extends SimplePropertyExpression<Entity, String> {

	static {
		register(ExprUUID.class, String.class, "uuid", "entities");
	}

	@Override
	public @Nullable String convert(Entity from) {
		return from.getUuid().toString();
	}

	@Override
	protected String getPropertyName() {
		return "uuid";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
