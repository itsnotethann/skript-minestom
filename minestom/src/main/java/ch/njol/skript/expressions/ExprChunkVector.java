package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.EffSecCreateInstance;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.generator.GenerationUnit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Chunk Vector")
@Description("The start or end block vector of a chunk being generated. Only available within a generator section.")
@Examples("set {_start} to chunk start vector")
public class ExprChunkVector extends SimpleExpression<Point> {

	static {
		Skript.registerExpression(ExprChunkVector.class, Point.class, ExpressionType.SIMPLE, "chunk (1:start|2:end) [block] vec[tor]");
	}

	private boolean isStart;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!getParser().isCurrentEvent(EffSecCreateInstance.TerrainGenerateEvent.class)) {
			Skript.error("You can only use the chunk vector expression within the generator section of the instance creator section.");
			return false;
		}
		isStart = parseResult.mark == 1;
		return true;
	}

	@Override
	protected @Nullable Point[] get(Event event) {
		GenerationUnit unit = ((EffSecCreateInstance.TerrainGenerateEvent) event).getUnit();
		Point vec = isStart ? unit.absoluteStart() : unit.absoluteEnd();
		return new Point[]{vec};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "chunk " + (isStart ? "start" : "end") + " block vector";
	}

}
