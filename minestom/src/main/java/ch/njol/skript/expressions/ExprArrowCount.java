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

@Name("Arrow Count")
@Description("The number of arrows in a living entity.")
@Examples("set arrow count of player to 5")
public class ExprArrowCount extends SimplePropertyExpression<LivingEntity, Integer> {

	static {
		register(ExprFireTicks.class, Integer.class, "arrow count", "livingentities");
	}

	@Override
	public @Nullable Integer convert(LivingEntity from) {
		return from.getArrowCount();
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
		Integer arrowCount = delta == null ? null : (Integer) delta[0];
		for (LivingEntity entity : entities) {
			if (mode == Changer.ChangeMode.RESET) {
				entity.setArrowCount(0);
				continue;
			}
			if (arrowCount == null) return;
			switch (mode) {
				case ADD -> entity.setArrowCount(entity.getArrowCount()+arrowCount);
				case REMOVE -> entity.setArrowCount(entity.getArrowCount()-arrowCount);
				case SET -> entity.setArrowCount(arrowCount);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "arrow count";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}
	
}
