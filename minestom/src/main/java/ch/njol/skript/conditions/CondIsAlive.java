package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Is Alive/Dead")
@Description("Checks if an entity is alive or dead.")
@Examples("if player is alive:")
public class CondIsAlive extends PropertyCondition<Entity> {

	static {
		register(CondIsAlive.class, "(alive|1¦dead)", "entities");
	}

	private boolean isNegated;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		isNegated = parseResult.mark == 1;
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public boolean check(Entity e) {
		if (e instanceof LivingEntity living) return isNegated == living.isDead();
		return isNegated == e.isRemoved();
	}

	@Override
	protected String getPropertyName() {
		return isNegated ? "dead" : "alive";
	}
}
