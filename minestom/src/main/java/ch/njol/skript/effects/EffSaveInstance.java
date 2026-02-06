package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Save Instance")
@Description("Saves the given instances.")
@Examples("save current instance")
public class EffSaveInstance extends Effect {

	static {
		Skript.registerEffect(EffSaveInstance.class, "save %instances%['[s] chunks] [to storage]");
	}

	private Expression<Instance> instance;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		instance = (Expression<Instance>) expressions[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Instance instance : this.instance.getArray(event)) {
			instance.saveChunksToStorage();
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "save " + instance.toString(event, debug) + " to storage";
	}

}
