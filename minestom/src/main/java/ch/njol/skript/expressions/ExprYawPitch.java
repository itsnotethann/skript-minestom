package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.VectorMath;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import static ch.njol.skript.util.VectorMath.*;

public class ExprYawPitch extends PropertyExpression<Object, Number> {

	static {
		register(ExprYawPitch.class, Number.class, "(1:yaw|2:pitch)", "points");
	}

	private int match;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		match = parseResult.mark;
		setExpr(expressions[0]);
		return true;
	}

	@Override
	protected Number[] get(Event event, Object[] source) {
		Object[] objects = getExpr().getArray(event);
		int length = objects.length;
		Number[] numbers = new Number[length];
		for (int i = 0; i < length; i++) {
			Object o = objects[i];
			if (o instanceof Entity entity) numbers[i] = getYawPitch(entity.getPosition());
			else if (o instanceof Vec vec) numbers[i] = getYawPitch(vec);
			else if (o instanceof Point point) numbers[i] = getYawPitch(point.asPos());
			else numbers[i] = 0;
		}
		return numbers;
	}

	private Number getYawPitch(Pos pos) {
		if (match == 1) return pos.yaw();
		return pos.pitch();
	}

	private Number getYawPitch(Vec vec) {
		if (match == 1) return skriptYaw(getYaw(vec));
		return skriptPitch(getPitch(vec));
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return match == 1 ? "yaw " : "pitch " + "of " + getExpr().toString(event, debug);
	}

}
