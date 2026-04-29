package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.EntityMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Frozen Time")
@Description("The amount of time an entity has been frozen for. A higher value will result in a higher frozen effect on-screen.")
@Examples("set frozen time of player to 3 seconds")
public class ExprFrozenTime extends SimplePropertyExpression<Entity, Timespan> {

	static {
		register(ExprFrozenTime.class, Timespan.class, "fr(eeze|ozen) time", "entities");
	}

	@Override
	public @Nullable Timespan convert(Entity from) {
		return NumberUtils.timespanFrom(from.getEntityMeta().getTickFrozen());
	}

	@Override
	public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Timespan.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Entity[] entities = getExpr().getArray(event);
		Timespan frozenTime = delta == null ? null : (Timespan) delta[0];
		Integer frozenTicks = frozenTime == null ? null : Math.toIntExact(NumberUtils.ticksFrom(frozenTime));
		for (Entity entity : entities) {
			EntityMeta entityMeta = entity.getEntityMeta();
			if (mode == Changer.ChangeMode.RESET) {
				entityMeta.setTickFrozen(0);
				continue;
			}
			if (frozenTime == null) return;
			int current = entityMeta.getTickFrozen();
			switch (mode) {
				case ADD -> entityMeta.setTickFrozen(current+frozenTicks);
				case REMOVE -> entityMeta.setTickFrozen(current-frozenTicks);
				case SET -> entityMeta.setTickFrozen(frozenTicks);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "frozen time";
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

}
