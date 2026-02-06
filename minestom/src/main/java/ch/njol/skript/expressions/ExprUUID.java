package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

@Name("UUID")
@Description("The UUID of an entity.")
@Examples("broadcast \"UUID: %uuid of player%\"")
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
