package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.VectorMath;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Vec;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprVectorRotatedAroundAnother extends SimpleExpression<Vec> {

	static {
		Skript.registerExpression(ExprVectorRotatedAroundAnother.class, Vec.class, ExpressionType.COMBINED,
			"%vectors% rotated around %vector% by %number% [degrees]");
	}

	@SuppressWarnings("null")
	private Expression<Vec> vectors, axis;

	@SuppressWarnings("null")
	private Expression<Number> degree;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		vectors = (Expression<Vec>) exprs[0];
		axis = (Expression<Vec>) exprs[1];
		degree = (Expression<Number>) exprs[2];
		return true;
	}

	@Override
	protected @Nullable Vec[] get(Event event) {
		Vec axis = this.axis.getSingle(event);
		Number angle = degree.getSingle(event);
		if (axis == null || angle == null)
			return new Vec[0];
		Vec[] vectors = this.vectors.getArray(event);
		for (int i = 0; i < vectors.length; i++) {
			vectors[i] = VectorMath.rot(vectors[i], axis, angle.doubleValue());
		}
		return vectors;
	}

	@Override
	public boolean isSingle() {
		return vectors.isSingle();
	}

	@Override
	public Class<? extends Vec> getReturnType() {
		return Vec.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return vectors.toString(event, debug) + " rotated around " + axis.toString(event, debug) + " by " + degree.toString(event, debug) + "degrees";
	}

}
