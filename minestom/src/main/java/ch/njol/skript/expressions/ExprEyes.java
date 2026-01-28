package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ExprEyes extends SimplePropertyExpression<Entity, Pos> {

	static {
		register(ExprEyes.class, Pos.class, "(head|eye[s]) [position[s]]", "entities");
	}

	@Override
	public @Nullable Pos convert(Entity from) {
		return from.getPosition().add(0, from.getEyeHeight(), 0);
	}

	@Override
	protected String getPropertyName() {
		return "eye position";
	}

	@Override
	public Class<? extends Pos> getReturnType() {
		return Pos.class;
	}

}
