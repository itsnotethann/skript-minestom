package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

@Name("Load/Unload Chunk")
@Description("Unload or load a chunk in the given instance")
@Examples("load chunk at vector(100, 0, 100) in {_instance}")
public class EffLoadChunk extends Effect {

	static {
		Skript.registerEffect(EffLoadChunk.class, "[:un]load chunk[s] (of|%-directions%) %points% [in [(world|instance)[s]] %instances%] [:sync]");
	}

	private Expression<Point> points;
	private Expression<Instance> instances;

	private boolean unload;
	private boolean sync;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		points = (Expression<Point>) expressions[1];
		if (expressions[0] != null) points = Direction.combine((Expression<? extends Direction>) expressions[0], points);
		instances = (Expression<Instance>) expressions[2];
		unload = parseResult.hasTag("un");
		sync = parseResult.hasTag("sync");
		return true;
	}

	@Override
	protected void execute(Event event) {
		Point[] points = this.points.getArray(event);
		for (Instance instance : instances.getArray(event)) {
			for (Point point : points) {
				if (unload) {
					if (!instance.isChunkLoaded(point)) continue;
					Chunk chunk = instance.getChunkAt(point);
					assert chunk != null;
					instance.unloadChunk(chunk);
				} else {
					CompletableFuture<Chunk> future = instance.loadChunk(point);
					if (sync) future.join();
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (unload ? "un" : "") + "load chunks at " + points.toString(event, debug) + " in instances" + instances.toString(event, debug)
			+ (sync ? " sync" : "");
	}

}
