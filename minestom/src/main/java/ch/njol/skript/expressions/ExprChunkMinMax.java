package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.EffSecCreateInstance;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.generator.GenerationUnit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprChunkMinMax extends SimpleExpression<Point> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprChunkMinMax.class, Point.class, ExpressionType.SIMPLE,
			"[absolute] (:start|end)[ing point] of [generat(or|ion)] chunk");
	}

	private boolean start;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		start = parseResult.hasTag("start");
		return true;
	}

	@Override
	protected Point @Nullable [] get(Event event) {
		GenerationUnit unit = ((EffSecCreateInstance.TerrainGenerateEvent) event).getUnit();
		Point[] point = new Point[1];
		point[0] = start ? unit.absoluteStart() : unit.absoluteEnd();
		return point;
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
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "absolute " + (start ? "start" : "end") + "ing point of generation chunk";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{EffSecCreateInstance.TerrainGenerateEvent.class};
	}

}
