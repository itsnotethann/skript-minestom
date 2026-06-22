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
import net.minestom.server.coordinate.Vec;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;


@Name("Normalized Vector")
@Description("Returns normalized (unit length) vectors.")
@Examples("set {_v} to normalized vector(3, 4, 0)")
public class ExprVectorNormalized extends SimpleExpression<Vec> {

	static {
		Skript.registerExpression(ExprVectorNormalized.class, Vec.class, ExpressionType.SIMPLE, "normalized %vectors%");
	}

	private Expression<Vec> vectors;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		vectors = (Expression<Vec>) exprs[0];
		return true;
	}

	@Override
	@Nullable
	protected Vec[] get(Event event) {
		Vec[] vectors = this.vectors.getArray(event);
		Vec[] vecs = new Vec[vectors.length];
		for (int i = 0; i < vectors.length; i++) {
			vecs[i] = vectors[i].normalize();
		}
		return vecs;
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
	public String toString(@Nullable Event event, boolean debug) {
		return "normalized " + vectors.toString(event, debug);
	}

}
