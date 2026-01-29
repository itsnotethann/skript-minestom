package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.sections.EffSecCreateInstance;
import ch.njol.util.Kleenean;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.UnitModifier;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class EffFillChunk extends Effect implements EventRestrictedSyntax {

	static {
		Skript.registerEffect(EffFillChunk.class,
			"fill [generat(or|ion)] chunk [blocks] [1:between ((y|height[s]) [level[s]]) %-integer% and %-integer%] with %block%");
	}

	private Expression<Integer> minHeight;
	private Expression<Integer> maxHeight;
	private Expression<Block> block;

	private boolean betweenY = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		betweenY = parseResult.mark == 1;
		minHeight = (Expression<Integer>) expressions[0];
		maxHeight = (Expression<Integer>) expressions[1];
		block = (Expression<Block>) expressions[2];
		return true;
	}

	@Override
	protected void execute(Event event) {
		Block block = this.block.getSingle(event);
		if (block == null) return;
		UnitModifier modifier = ((EffSecCreateInstance.TerrainGenerateEvent) event).getUnit().modifier();
		if (betweenY) {
			Integer min = minHeight.getSingle(event);
			if (min == null) return;
			Integer max = maxHeight.getSingle(event);
			if (max == null) return;
			modifier.fillHeight(min, max, block);
			return;
		}
		modifier.fill(block);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "fill chunk blocks" +
			(betweenY ? " between y levels " + minHeight.toString(event, debug) + " and " + maxHeight.toString(event, debug) : "") +
			" with " + block.toString(event, debug);
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{EffSecCreateInstance.TerrainGenerateEvent.class};
	}

}
