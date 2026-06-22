package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Look At")
@Description("Forces the mob(s) or player(s) to look at an entity, vector or location. Vanilla max head pitches range from 10 to 50.")
@Example("force the player to look towards event-entity's feet")
@Example("""
	on entity explosion:
		set {_player} to the nearest player
		{_player} is set
		distance between {_player} and the event-position is less than 15
		make {_player} look towards vector from the {_player} to event-entity's position
	""")
@Example("force {_enderman} to face the block 3 meters above {_location}")
public class EffLook extends Effect {

	static {
		Skript.registerEffect(EffLook.class,
			"(force|make) %livingentities% [to] (face [towards]|look [(at|towards)]) %entity%'s (feet:feet|eyes)",
			"(force|make) %livingentities% [to] (face [towards]|look [(at|towards)]) [the] (feet:feet|eyes) of %entity%",
			"(force|make) %livingentities% [to] (face [towards]|look [(at|towards)]) %point/entity%");
	}

	private Player.FacePoint anchor = Player.FacePoint.EYE;
	private Expression<LivingEntity> entities;

	/**
	 * Can be Vector, Location or an Entity.
	 */
	private Expression<?> target;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entities = (Expression<LivingEntity>) exprs[0];
		target = exprs[1];
		if (parseResult.hasTag("feet")) anchor = Player.FacePoint.FEET;
		return true;
	}

	@Override
	protected void execute(Event event) {
		Object object = target.getSingle(event);
		if (object == null)
			return;

		for (LivingEntity entity : entities.getArray(event)) {
			lookAt(entity, anchor, object);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "force " + entities.toString(event, debug) + " to look at " + target.toString(event, debug);
	}

	private void lookAt(LivingEntity entity, Player.FacePoint facePoint, Object target) {
		if (entity instanceof Player player) {
			if (target instanceof Entity e) player.facePosition(Player.FacePoint.EYE, e, facePoint);
			else player.facePosition(Player.FacePoint.EYE, (Point) target);
		} else {
			if (target instanceof Entity e) entity.lookAt(e);
			else entity.lookAt((Point) target);
		}
	}

}
