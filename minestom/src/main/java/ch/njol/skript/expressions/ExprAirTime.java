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


@Name("Air Time")
@Description("The amount of time an entity has left for air.")
@Examples("set air time of player to 3 seconds")
public class ExprAirTime extends SimplePropertyExpression<Entity, Timespan> {

	static {
		register(ExprAirTime.class, Timespan.class, "air time", "entities");
	}

	@Override
	public @Nullable Timespan convert(Entity from) {
		return NumberUtils.timespanFrom(from.getEntityMeta().getAirTicks());
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Timespan.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Entity[] entities = getExpr().getArray(event);
		Timespan airTime = delta == null ? null : (Timespan) delta[0];
		Integer airTicks = airTime == null ? null : Math.toIntExact(NumberUtils.ticksFrom(airTime));
		for (Entity entity : entities) {
			EntityMeta entityMeta = entity.getEntityMeta();
			if (mode == Changer.ChangeMode.RESET) {
				entityMeta.setAirTicks(300);
				continue;
			}
			if (airTime == null) return;
			int current = entityMeta.getAirTicks();
			switch (mode) {
				case ADD -> entityMeta.setAirTicks(current+airTicks);
				case REMOVE -> entityMeta.setAirTicks(current-airTicks);
				case SET -> entityMeta.setAirTicks(airTicks);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "air time";
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

}
