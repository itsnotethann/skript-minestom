package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.minestom.server.entity.Entity;

@Name("On Ground")
@Description("Checks if an entity is on the ground. For the player this is reported by them, so it is unreliable.")
@Examples("if entity is on ground:")
public class CondOnGround extends PropertyCondition<Entity> {

	static {
		register(CondOnGround.class, "on [the] ground", "entities");
	}

	@Override
	public boolean check(Entity e) {
		return e.isOnGround();
	}

	@Override
	protected String getPropertyName() {
		return "on ground";
	}
}
