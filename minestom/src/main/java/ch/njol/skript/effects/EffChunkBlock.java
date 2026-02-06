package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.sections.EffSecCreateInstance;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.UnitModifier;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
@Name("Set/Assign Chunk Block")
@Description("Sets a block at a specific point within a chunk generator.")
@Examples("set chunk block at point(0, 64, 0) to stone")
public class EffChunkBlock extends Effect implements EventRestrictedSyntax {

	static {
		Skript.registerEffect(EffChunkBlock.class, "(set|assign) chunk [generat(or|ion)] block at %points% to %block%");
	}

	private Expression<Point> points;
	private Expression<Block> block;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		points = (Expression<Point>) expressions[0];
		block = (Expression<Block>) expressions[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		Block block = this.block.getSingle(event);
		if (block == null) return;
		Point[] points = this.points.getArray(event);
		UnitModifier modifier = ((EffSecCreateInstance.TerrainGenerateEvent) event).getUnit().modifier();
		for (Point point : points) {
			modifier.setBlock(point, block);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "set chunk block at " + points.toString(event, debug) + " to " + block.toString(event, debug);
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{EffSecCreateInstance.TerrainGenerateEvent.class};
	}

}
