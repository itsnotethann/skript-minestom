package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.utils.entity.EntityUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Passenger Height Offset")
@Description("Allows you to get the passenger offset for an entity type that would be riding on the provided entity at the given index.")
@Examples("spawn zombie at passenger position offset for zombie riding on {_entity} for index 0 in {_entity}'s instance")
public class ExprPassengerHeightOffset extends SimpleExpression<Point> {

	static {
		Skript.registerExpression(ExprPassengerHeightOffset.class, Point.class, ExpressionType.COMBINED,
			"passenger [height|position] offset (of|for) %entitytypes% [riding] (on|from) %entity% for [passenger] index %integer%");
	}

	private Expression<EntityType> types;
	private Expression<Entity> entity;
	private Expression<Integer> index;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		types = (Expression<EntityType>) expressions[0];
		entity = (Expression<Entity>) expressions[1];
		index = (Expression<Integer>) expressions[2];
		return true;
	}

	@Override
	protected Point @Nullable [] get(Event event) {
		Entity entity = this.entity.getSingle(event);
		Integer index = this.index.getSingle(event);
		if (entity == null || index == null) return new Point[0];
		List<Point> offsets = new ArrayList<>();
		for (EntityType type : types.getArray(event)) {
			offsets.add(EntityUtils.getPassengerPositionOffset(entity, type, index));
		}
		return offsets.toArray(new Point[0]);
	}

	@Override
	public boolean isSingle() {
		return types.isSingle();
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "passenger height offset for " + types.toString(event, debug) + " riding on " + entity.toString(event, debug)
			+ " for passenger index " + index.toString(event, debug);
	}

}
