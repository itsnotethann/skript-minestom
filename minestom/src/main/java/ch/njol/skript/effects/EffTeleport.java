package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class EffTeleport extends Effect {

	static {
		Skript.registerEffect(EffTeleport.class, "teleport %entities% to %point% [in [(world|instance)] %-instance%]");
	}

	private Expression<Entity> entities;
	private Expression<Point> point;
	@Nullable
	private Expression<Instance> instance;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		entities = (Expression<Entity>) expressions[0];
		point = (Expression<Point>) expressions[1];
		instance = (Expression<Instance>) expressions[2];
		return true;
	}

	@Override
	protected void execute(Event event) {
		Point point = this.point.getSingle(event);
		if (point == null) return;
		Pos pos = point.asPos();
		Instance i = instance != null ? instance.getSingle(event) : null;
		for (Entity entity : entities.getArray(event)) {
			if (i == null || i.equals(entity.getInstance())) entity.teleport(pos);
			else entity.setInstance(i, pos);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "teleport " + entities.toString(event, debug) + " to " + point.toString(event, debug) +
			(instance != null ? " in instance " + instance.toString(event, debug) : "");
	}

}
