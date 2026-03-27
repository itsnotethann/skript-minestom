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
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Set/Assign Chunk Block")
@Description("Sets a block at a specific point within a chunk generator.")
@Examples("set chunk block at vector(0, 64, 0) to stone")
public class EffChunkFiller extends Effect implements EventRestrictedSyntax {

	static {
		Skript.registerEffect(EffChunkFiller.class,
			"(set|assign) [:absolute|relative] chunk [generat(or|ion)] (biome|block) at %points% to %block/biome%");
	}

	private Expression<Point> points;
	private Expression<?> filler;
	private boolean relative;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		points = (Expression<Point>) expressions[0];
		filler = expressions[1];
		relative = !parseResult.hasTag("absolute");
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(Event event) {
		Object filler = this.filler.getSingle(event);
		if (filler == null) return;
		if (filler instanceof Biome biome) {
			RegistryKey<Biome> key = MinecraftServer.getBiomeRegistry().getKey(biome);
			if (key == null) return;
			filler = key;
		}
		Point[] points = this.points.getArray(event);
		GenerationUnit unit = ((EffSecCreateInstance.TerrainGenerateEvent) event).getUnit();
		UnitModifier modifier = unit.modifier();
		if (relative) {
			Point start = unit.absoluteStart();
			Point end = unit.absoluteEnd();
			int startX = start.blockX();
			int startZ = start.blockZ();
			int endX = end.blockX();
			int endZ = end.blockZ();
			for (Point point : points) {
				int x = startX + ((int) point.x());
				int y = (int) point.y();
				int z = startZ + ((int) point.z());
				if (x < startX || x >= endX || z < startZ || z >= endZ) continue; // out of bounds
				if (filler instanceof Block block) modifier.setBlock(x, y, z, block);
				else modifier.setBiome(x, y, z, (RegistryKey<Biome>) filler);
			}
		} else {
			for (Point point : points) {
				int x = point.blockX();
				int y = point.blockY();
				int z = point.blockZ();
				if (filler instanceof Block block) modifier.setBlock(x, y, z, block);
				else modifier.setBiome(x, y, z, (RegistryKey<Biome>) filler);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "set chunk block at " + points.toString(event, debug) + " to " + filler.toString(event, debug);
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{EffSecCreateInstance.TerrainGenerateEvent.class};
	}

}
