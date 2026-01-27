package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffUnregisterInstance extends Effect {

	static {
		Skript.registerEffect(EffUnregisterInstance.class, "unregister %instances%");
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
			if (!instance.isRegistered()) continue;
			MinecraftServer.getInstanceManager().unregisterInstance(instance);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "unregister " + instance.toString(event, debug);
	}

}
