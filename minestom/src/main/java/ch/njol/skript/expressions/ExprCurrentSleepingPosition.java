package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.LivingEntityMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprCurrentSleepingPosition extends SimplePropertyExpression<LivingEntity, Point> {

	static {
		register(ExprCurrentSleepingPosition.class, Point.class, "[current] [bed] sleep[ing] (position|point)", "livingentities");
	}

	@Override
	public @Nullable Point convert(LivingEntity from) {
		LivingEntityMeta livingEntityMeta = from.getLivingEntityMeta();
		if (livingEntityMeta == null) return null;
		return livingEntityMeta.getBedInWhichSleepingPosition();
	}

	@Override
	public Class<?> @org.jetbrains.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, RESET, DELETE -> CollectionUtils.array(Point.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @org.jetbrains.annotations.Nullable [] delta, Changer.ChangeMode mode) {
		LivingEntity[] entities = getExpr().getArray(event);
		Point point = delta == null ? null : (Point) delta[0];
		for (LivingEntity entity : entities) {
			LivingEntityMeta livingEntityMeta = entity.getLivingEntityMeta();
			if (livingEntityMeta == null) continue;
			if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
				livingEntityMeta.setBedInWhichSleepingPosition(null);
				continue;
			}
			if (point == null) return;
			livingEntityMeta.setBedInWhichSleepingPosition(point);
		}
	}

	@Override
	protected String getPropertyName() {
		return "current bed sleeping position";
	}

	@Override
	public Class<? extends Pos> getReturnType() {
		return Pos.class;
	}

}
