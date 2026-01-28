package ch.njol.skript.expressions;

import ch.njol.skript.util.VectorMath;
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

@Name("Vectors - Vec from Pitch and Yaw")
@Description("Creates a vector from a yaw and pitch value.")
@Examples("set {_v} to vector from yaw 45 and pitch 45")
@Since("2.2-dev28")
public class ExprVectorFromYawAndPitch extends SimpleExpression<Vec> {

	static {
		Skript.registerExpression(ExprVectorFromYawAndPitch.class, Vec.class, ExpressionType.COMBINED,
			"[a] [new] vector (from|with) yaw %number% and pitch %number%");
	}

	@SuppressWarnings("null")
	private Expression<Number> pitch, yaw;

	@Override
	@SuppressWarnings({"unchecked", "null"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		yaw = (Expression<Number>) exprs[0];
		pitch = (Expression<Number>) exprs[1];
		return true;
	}

	@Override
	@SuppressWarnings("null")
	protected Vec[] get(Event event) {
		Number skriptYaw = yaw.getSingle(event);
		Number skriptPitch = pitch.getSingle(event);
		if (skriptYaw == null || skriptPitch == null)
			return null;
		float yaw = VectorMath.fromSkriptYaw(VectorMath.wrapAngleDeg(skriptYaw.floatValue()));
		float pitch = VectorMath.fromSkriptPitch(VectorMath.wrapAngleDeg(skriptPitch.floatValue()));
		return CollectionUtils.array(VectorMath.fromYawAndPitch(yaw, pitch));
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
		return "vector from yaw " + yaw.toString(event, debug) + " and pitch " + pitch.toString(event, debug);
	}

}
