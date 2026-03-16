package ch.njol.skript.expressions;


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
import net.minestom.server.coordinate.Vec;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Vectors - Dot Product")
@Description("Gets the dot product between two vectors.")
@Examples("set {_dot} to {_v1} dot {_v2}")
@Since("2.2-dev28")
public class ExprVectorDotProduct extends SimpleExpression<Number> {

	static {
		Skript.registerExpression(ExprVectorDotProduct.class, Number.class, ExpressionType.COMBINED, "%vector% dot %vector%");
	}

	@SuppressWarnings("null")
	private Expression<Vec> first, second;

	@Override
	@SuppressWarnings({"unchecked", "null"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		first = (Expression<Vec>) exprs[0];
		second = (Expression<Vec>) exprs[1];
		return true;
	}

	@Override
	@SuppressWarnings("null")
	protected Number[] get(Event event) {
		Vec first = this.first.getSingle(event);
		Vec second = this.second.getSingle(event);
		if (first == null || second == null)
			return null;
		return CollectionUtils.array(first.x() * second.x() + first.y() * second.y() + first.z() * second.z());
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return first.toString(event, debug) + " dot " + second.toString(event, debug);
	}

}