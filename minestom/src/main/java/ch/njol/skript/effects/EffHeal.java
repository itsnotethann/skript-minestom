package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Heal")
@Description("Heals the given living entities to their maximum health.")
@Examples("heal all players")
public class EffHeal extends Effect {

	static {
		Skript.registerEffect(EffHeal.class, "heal %livingentities%");
	}

	private Expression<LivingEntity> entities;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		entities = (Expression<LivingEntity>) expressions[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (LivingEntity entity : entities.getArray(event)) {
			entity.heal();
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "heal " + entities.toString(event, debug);
	}

}
