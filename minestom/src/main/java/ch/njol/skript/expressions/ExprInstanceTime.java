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
import net.minestom.server.instance.Clock;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Instance Time")
@Description("The time or time rate of an instance.")
@Examples("""
	set time of {_instance} to 6000 # noon
	set time rate of {_instance} to 0 # sun won't move""")
public class ExprInstanceTime extends PropertyExpression<Instance, Number> {

	static {
		register(ExprInstanceTime.class, Number.class, "time [:rate]", "instances");
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
	protected Number[] get(Event event, Instance[] source) {
		List<Number> times = new ArrayList<>();
		for (Instance instance : source) {
			if (rate) times.add(getTimeRate(instance));
			else times.add(instance.getTime());
		}
		return times.toArray(new Number[0]);
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Instance[] instances = getExpr().getArray(event);
		Number amount = (Number) delta[0];
		for (Instance instance : instances) {
			if (mode == Changer.ChangeMode.RESET) {
				if (rate) setTimeRate(instance, 1);
				else instance.setTime(0);
				continue;
			}
			if (amount == null) return;
			switch (mode) {
				case ADD -> {
					if (rate) setTimeRate(instance, getTimeRate(instance) + amount.floatValue());
					else instance.setTime(instance.getTime()+amount.intValue());
				}
				case REMOVE -> {
					if (rate) setTimeRate(instance, getTimeRate(instance) - amount.floatValue());
					else instance.setTime(instance.getTime()-amount.intValue());
				}
				case SET -> {
					if (rate) setTimeRate(instance, amount.floatValue());
					else instance.setTime(amount.intValue());
				}
			}
		}
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "time " + (rate ? "rate " : "") + "of " + getExpr().toString(event, debug);
	}

	private float getTimeRate(Instance instance) {
		Clock clock = instance.defaultClock();
		return clock == null ? -1 : clock.rate();
	}

	private void setTimeRate(Instance instance, Number amount) {
		Clock clock = instance.defaultClock();
		if (clock == null) return;
		clock.rate(amount.floatValue());
	}

}
