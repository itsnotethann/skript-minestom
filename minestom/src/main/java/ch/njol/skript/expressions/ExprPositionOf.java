package ch.njol.skript.expressions;


import net.minestom.server.coordinate.Point;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.WrapperExpression;
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
public class ExprPositionOf extends WrapperExpression<Point> {
	static {
		Skript.registerExpression(ExprPositionOf.class, Point.class, ExpressionType.PROPERTY, "position of %point%", "%point%'[s] position");
	}

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends Point>) exprs[0]);
		return true;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the position of " + getExpr().toString(e, debug);
	}

}
