package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprInstanceEntities extends PropertyExpression<Instance, Entity> {

	static {
		Skript.registerExpression(ExprInstanceEntities.class, Entity.class, ExpressionType.PROPERTY,
			"[(every|all)] entities [(of|[with]in) %instances%]",
			"%instances%'[s] entities");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Instance>) expressions[0]);
		return true;
	}

	@Override
	protected Entity[] get(Event event, Instance[] source) {
		List<Entity> entities = new ArrayList<>();
		for (Instance instance : getExpr().getArray(event)) {
			entities.addAll(instance.getEntities());
		}
		return entities.toArray(new Entity[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "entities of " + getExpr().toString(event, debug);
	}

}
