package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.minestom.server.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Fire Time")
@Description("The amount of time an entity is on fire.")
@Examples("set fire time of player to 3 seconds")
public class ExprFireTime extends SimplePropertyExpression<LivingEntity, Timespan> {

	static {
		register(ExprFireTime.class, Timespan.class, "fire time", "livingentities");
	}

	@Override
	public @Nullable Timespan convert(LivingEntity from) {
		return NumberUtils.timespanFrom(from.getFireTicks());
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
		LivingEntity[] entities = getExpr().getArray(event);
		Timespan fireTime = delta == null ? null : (Timespan) delta[0];
		Integer fireTicks = fireTime == null ? null : Math.toIntExact(NumberUtils.ticksFrom(fireTime));
		for (LivingEntity entity : entities) {
			if (mode == Changer.ChangeMode.RESET) {
				entity.setFireTicks(0);
				continue;
			}
			if (fireTime == null) return;
			switch (mode) {
				case ADD -> entity.setFireTicks(entity.getFireTicks()+fireTicks);
				case REMOVE -> entity.setFireTicks(entity.getFireTicks()-fireTicks);
				case SET -> entity.setFireTicks(fireTicks);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "fire time";
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

}
