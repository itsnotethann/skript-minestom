package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Instance Time")
@Description("The time or time rate of an instance.")
@Examples("set time of current instance to 1000")
public class ExprInstanceTime extends PropertyExpression<Instance, Long> {

	static {
		register(ExprInstanceTime.class, Long.class, "time [:rate]", "instances");
	}

	private boolean rate;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Instance>) expressions[0]);
		rate = parseResult.hasTag("rate");
		return true;
	}
	
	@Override
	protected Long[] get(Event event, Instance[] source) {
		List<Long> times = new ArrayList<>();
		for (Instance instance : source) {
			if (rate) times.add((long) instance.getTimeRate());
			else times.add(instance.getTime());
		}
		return times.toArray(new Long[0]);
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Long.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Instance[] instances = getExpr().getArray(event);
		Long amount = delta == null ? null : Math.max(0, ((Long) delta[0]));
		for (Instance instance : instances) {
			if (mode == Changer.ChangeMode.RESET) {
				if (rate) instance.setTimeRate(1);
				else instance.setTime(0);
				continue;
			}
			if (amount == null) return;
			switch (mode) {
				case ADD -> {
					if (rate) instance.setTimeRate(Math.toIntExact(instance.getTimeRate() + amount));
					else instance.setTime(instance.getTime()+amount);
				}
				case REMOVE -> {
					if (rate) instance.setTimeRate(Math.toIntExact(instance.getTimeRate() - amount));
					else instance.setTime(instance.getTime()-amount);
				}
				case SET -> {
					if (rate) instance.setTimeRate(Math.toIntExact(amount));
					else instance.setTime(amount);
				}
			}
		}
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "time " + (rate ? "rate " : "") + "of " + getExpr().toString(event, debug);
	}

}
