package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

@Name("Eye Position")
@Description("The eye position of an entity.")
@Examples("set {_pos} to eye position of player")
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
