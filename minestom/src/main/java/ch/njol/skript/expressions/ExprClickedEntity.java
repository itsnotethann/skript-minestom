package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.EntityAttackWrapper;
import ch.njol.skript.events.wrapper.PlayerEntityInteractWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprClickedEntity extends SimpleExpression<Entity> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprClickedEntity.class, Entity.class, ExpressionType.SIMPLE, "clicked entity");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable Entity[] get(Event event) {
		Entity e = null;
		if (event instanceof EntityAttackWrapper attackWrapper) e = attackWrapper.getEvent().getTarget();
		else if (event instanceof PlayerEntityInteractWrapper interactWrapper) e = interactWrapper.getEvent().getTarget();
		if (e == null) return new Entity[0];
		return new Entity[]{e};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "clicked entity";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{EntityAttackWrapper.class, PlayerEntityInteractWrapper.class};
	}

}
