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
import net.minestom.server.coordinate.Vec;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Centered Point")
@Description("Returns the center of a block position (adds 0.5 to x and z).")
@Examples("set {_p} to centered player's position")
public class ExprCentered extends SimpleExpression<Point> {

	static {
		Skript.registerExpression(ExprCentered.class, Point.class, ExpressionType.COMBINED, "centered %points%");
	}

	private Expression<Point> points;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		points = (Expression<Point>) expressions[0];
		return true;
	}

	@Override
	protected @Nullable Point[] get(Event event) {
		Point[] points = this.points.getArray(event);
		Point[] ret = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			ret[i] = toCenter(points[i]);
		}
		return ret;
	}

	@Override
	public boolean isSingle() {
		return points.isSingle();
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "centered " + points.toString(event, debug);
	}

	private Point toCenter(Point point) {
		return new Vec(point.blockX()+0.5, point.y(), point.blockZ()+0.5);
	}

}
