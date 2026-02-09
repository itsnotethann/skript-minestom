package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Coordinate")
@Description("The x, y, or z coordinate of a point.")
@Examples("set {_x} to x-coordinate of player's position")
public class ExprCoordinate extends PropertyExpression<Point, Number> {

	static {
		register(ExprCoordinate.class, Number.class, "(1:x|2:y|3:z)[(-| )(coord[inate]|pos[ition])[s]]", "points");
	}

	private int match;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		match = parseResult.mark;
		setExpr((Expression<? extends Point>) expressions[0]);
		return true;
	}

	@Override
	protected Number[] get(Event event, Point[] source) {
		Point[] points = getExpr().getArray(event);
		int length = points.length;
		Number[] numbers = new Number[length];
		for (int i = 0; i < length; i++) {
			Point point = points[i];
			if (match == 1) numbers[i] = point.x();
			else if (match == 2) numbers[i] = point.y();
			else if (match == 3) numbers[i] = point.z();
		}
		return numbers;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String coordinateType = match == 1 ? "x" : (match == 2 ? "y" : "z");
		return coordinateType + " coordinate of " + getExpr().toString(event, debug);
	}

}
