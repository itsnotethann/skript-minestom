package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Fire Ticks")
@Description("The number of ticks an entity is on fire.")
@Examples("set fire ticks of player to 100")
public class ExprFireTicks extends SimplePropertyExpression<LivingEntity, Integer> {

	static {
		register(ExprFireTicks.class, Integer.class, "fire ticks", "livingentities");
	}

	@Override
	public @Nullable Integer convert(LivingEntity from) {
		return from.getFireTicks();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		LivingEntity[] entities = getExpr().getArray(event);
		Integer fireTicks = delta == null ? null : (Integer) delta[0];
		for (LivingEntity entity : entities) {
			if (mode == Changer.ChangeMode.RESET) {
				entity.setFireTicks(0);
				continue;
			}
			if (fireTicks == null) return;
			switch (mode) {
				case ADD -> entity.setFireTicks(entity.getFireTicks()+fireTicks);
				case REMOVE -> entity.setFireTicks(entity.getFireTicks()-fireTicks);
				case SET -> entity.setFireTicks(fireTicks);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "fire ticks";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
