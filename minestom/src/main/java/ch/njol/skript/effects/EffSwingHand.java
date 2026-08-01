package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Swing Hand")
@Description("Makes an entity swing their hand. This does nothing if the entity does not have an animation for swinging their hand.")
@Example("make player swing their main hand")
public class EffSwingHand extends Effect {

	static {
		Skript.registerEffect(EffSwingHand.class,
			"make %livingentities% swing [their] [main] hand",
			"make %livingentities% swing [their] off[ ]hand");
	}

	@SuppressWarnings("null")
	private Expression<LivingEntity> entities;
	private boolean isMainHand;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entities = (Expression<LivingEntity>) exprs[0];
		isMainHand = matchedPattern == 0;
		return true;
	}

	@Override
	protected void execute(Event e) {
		if (isMainHand) {
			for (LivingEntity entity : entities.getArray(e)) {
				entity.swingMainHand();
			}
		} else {
			for (LivingEntity entity : entities.getArray(e)) {
				entity.swingOffHand();
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + entities.toString(e, debug) + " swing their " + (isMainHand ? "hand" : "off hand");
	}

}
