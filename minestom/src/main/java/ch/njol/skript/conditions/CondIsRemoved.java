package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;

public class CondIsRemoved extends PropertyCondition<Entity> {

	static {
		register(CondIsRemoved.class, "removed", "entities");
	}


	@Override
	public boolean check(Entity e) {
		return e.isRemoved();
	}

	@Override
	protected String getPropertyName() {
		return "removed";
	}
}
