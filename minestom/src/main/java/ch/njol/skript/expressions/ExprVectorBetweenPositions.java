package ch.njol.skript.expressions;


import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Vectors - Vec Between Locations")
@Description("Creates a vector between two locations.")
@Examples("set {_v} to vector between {_loc1} and {_loc2}")
@Since("2.2-dev28")
public class ExprVectorBetweenPositions extends SimpleExpression<Vec> {

	static {
		Skript.registerExpression(ExprVectorBetweenPositions.class, Vec.class, ExpressionType.COMBINED,
			"[the] vector (from|between) %point% (to|and) %point%");
	}

	@SuppressWarnings("null")
	private Expression<Point> from, to;

	@Override
	@SuppressWarnings({"unchecked", "null"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		from = (Expression<Point>) exprs[0];
		to = (Expression<Point>) exprs[1];
		return true;
	}

	@Override
	@SuppressWarnings("null")
	protected Vec[] get(Event event) {
		Point from = this.from.getSingle(event);
		Point to = this.to.getSingle(event);
		if (from == null || to == null)
			return null;
		return CollectionUtils.array(new Vec(to.x() - from.x(), to.y() - from.y(), to.z() - from.z()));
	}

	@Override
	public boolean isSingle() {
		return true;
	}
	@Override
	public Class<? extends Vec> getReturnType() {
		return Vec.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "vector from " + from.toString(event, debug) + " to " + to.toString(event, debug);
	}

}