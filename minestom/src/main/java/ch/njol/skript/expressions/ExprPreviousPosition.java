package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;


@Name("Previous Position")
@Description("An entity's position from the previous tick.")
@Examples("set {_prev} to previous position of player")
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
