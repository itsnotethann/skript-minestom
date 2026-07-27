package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Switch Entity Type")
@Description("Changes the entity type of the given entities without despawning them. Slightly broken at times and may be removed.")
@Examples("switch player's entity type to pig")
public class EffSwitchEntityType extends Effect {

	static {
		Skript.registerEffect(EffSwitchEntityType.class, "(switch|change) %entities%'[s] entity[ ]type to %entitytype%");
	}

	private Expression<Entity> entities;
	private Expression<EntityType> type;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		entities = (Expression<Entity>) expressions[0];
		type = (Expression<EntityType>) expressions[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		EntityType type = this.type.getSingle(event);
		if (type == null) return;
		for (Entity entity : entities.getArray(event)) {
			entity.switchEntityType(type);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "switch " + entities.toString(event, debug) + "'s entity type to " + type.toString(event, debug);
	}

}
