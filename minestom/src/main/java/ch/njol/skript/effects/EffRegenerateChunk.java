package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.generator.Generator;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Regenerate Chunk")
@Description("Regenerates the given chunks using the instance's chunk generator.")
@Examples("regenerate chunk at player")
public class EffRegenerateChunk extends Effect {

	static {
		Skript.registerEffect(EffRegenerateChunk.class, "regen[erate] %chunks%");
	}

	private Expression<Chunk> chunks;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		chunks = (Expression<Chunk>) expressions[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Chunk chunk : chunks.getArray(event)) {
			Instance instance = chunk.getInstance();
			Generator generator = instance.generator();
			if (generator == null) continue;
			instance.generateChunk(chunk.getChunkX(), chunk.getChunkZ(), generator);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "regenerate " + chunks.toString(event, debug);
	}

}
