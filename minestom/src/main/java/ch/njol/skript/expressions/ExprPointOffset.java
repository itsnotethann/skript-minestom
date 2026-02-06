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
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Point Offset")
@Description("A point offset by a certain amount.")
@Examples("set {_p} to player's location offset by (1, 2, 3)")
public class ExprPointOffset extends SimpleExpression<Point> {

	static {
		Skript.registerExpression(ExprPointOffset.class, Point.class, ExpressionType.COMBINED, "%points% (offset by|~) %point%");
	}

	private Expression<Point> pointsExpression;
	private Expression<Point> offsetExpression;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		pointsExpression = (Expression<Point>) expressions[0];
		offsetExpression = (Expression<Point>) expressions[1];
		return true;
	}

	@Override
	protected @Nullable Point[] get(Event event) {
		Point offset = offsetExpression.getSingle(event);
		if (offset == null) return new Point[0];
		Point[] points = pointsExpression.getArray(event);
		int length = points.length;
		Point[] offsetPoints = new Point[length];
		for (int i = 0; i < length; i++) {
			offsetPoints[i] = points[i].add(offset);
		}
		return offsetPoints;
	}

	@Override
	public boolean isSingle() {
		return pointsExpression.isSingle();
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return pointsExpression.toString(event, debug) + " offset by " + offsetExpression.toString(event, debug);
	}

}
