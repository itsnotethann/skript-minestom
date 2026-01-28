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

import java.util.List;

public class ExprVectorRotatedXYZ extends SimpleExpression<Vec> {

	static {
		Skript.registerExpression(ExprVectorRotatedXYZ.class, Vec.class, ExpressionType.COMBINED,
			"%vectors% rotated around (0¦x|1¦y|2¦z)(-| )axis by %number% [degrees]");
	}

	private final static Character[] axes = new Character[] {'x', 'y', 'z'};

	@SuppressWarnings("null")
	private Expression<Vec> vectors;

	@SuppressWarnings("null")
	private Expression<Number> degree;
	private int axis;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		vectors = (Expression<Vec>) expressions[0];
		degree = (Expression<Number>) expressions[1];
		axis = parseResult.mark;
		return true;
	}

	@Override
	protected @Nullable Vec[] get(Event event) {
		Number angle = degree.getSingle(event);
		if (angle == null)
			return new Vec[0];
		Vec[] vectors = this.vectors.getArray(event);
		switch (axis) {
			case 0:
				for (int i = 0; i < vectors.length; i++) {
					vectors[i] = VectorMath.rotX(vectors[i], angle.doubleValue());
				}
				break;
			case 1:
				for (int i = 0; i < vectors.length; i++) {
					vectors[i] = VectorMath.rotY(vectors[i], angle.doubleValue());
				}
				break;
			case 2:
				for (int i = 0; i < vectors.length; i++) {
					vectors[i] = VectorMath.rotZ(vectors[i], angle.doubleValue());
				}
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
		return vectors.toString(event, debug) + " rotated around " + axes[axis] + "-axis" + " by " + degree.toString(event, debug) + "degrees";
	}

}
