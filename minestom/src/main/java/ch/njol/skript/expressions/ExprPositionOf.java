package ch.njol.skript.expressions;


import ch.njol.skript.expressions.base.PropertyExpression;
import net.minestom.server.coordinate.BlockVec;
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
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Point")
@Description({"The location of a block or entity. This not only represents the x, y and z coordinates of the location but also includes the world and the direction an entity is looking " +
	"(e.g. teleporting to a saved location will make the teleported entity face the same saved direction every time).",
	"Please note that the location of an entity is at it's feet, use <a href='#ExprEyePoint'>head location</a> to get the location of the head."})
@Examples({"set {home::%uuid of player%} to the location of the player",
	"message \"You home was set to %player's location% in %player's world%.\""})
@Since("")
public class ExprPositionOf extends PropertyExpression<Point, Point> {

	static {
		Skript.registerExpression(ExprPositionOf.class, Point.class, ExpressionType.PROPERTY,
			"[center:center[ed]] position of %point%", "%point%'[s] [center:center[ed]] position");
	}

	private boolean centered;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends Point>) exprs[0]);
		centered = parseResult.hasTag("center");
		return true;
	}

	@Override
	protected Point[] get(Event event, Point[] source) {
		Point[] points = new Point[source.length];
		for (int i = 0; i < source.length; i++) {
			Point point = source[i];
			points[i] = centered ? toCenter(point) : point;
		}
		return points;
	}

	private Point toCenter(Point point) {
		return new Vec(point.blockX()+0.5, point.y(), point.blockZ()+0.5);
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the " + (centered ? "block " : "") + "position of " + getExpr().toString(e, debug);
	}

}
