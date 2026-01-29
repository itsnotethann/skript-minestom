package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffKill extends Effect {

	static {
		Skript.registerEffect(EffKill.class, "(:remove|kill) %entities%");
	}

	private Expression<Entity> entities;

	private boolean remove;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		entities = (Expression<Entity>) expressions[0];
		remove = parseResult.hasTag("remove");
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Entity e : entities.getArray(event)) {
			if (remove) {
				if (e instanceof Player player) player.kill();
				else e.remove();
			} else {
				if (e instanceof LivingEntity living) living.kill();
				else e.remove();
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return remove ? "remove " : "kill " + entities.toString(event, debug);
	}

}
