package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

public class ExprTransformationInterpolation extends SimplePropertyExpression<Entity, Object> {

	static {
		register(ExprTransformationInterpolation.class, Object.class,
			"transform[ation] [interpolation] (:start|duration)", "entities");
	}

	private boolean start;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Entity>) expressions[0]);
		start = parseResult.hasTag("start");
		return true;
	}

	@Override
	public @org.jspecify.annotations.Nullable Object convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return start ? meta.getTransformationInterpolationStartDelta() : NumberUtils.timespanFrom(meta.getTransformationInterpolationDuration());
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
			if (start) return CollectionUtils.array(Integer.class);
			return CollectionUtils.array(Timespan.class);
		}
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Object object = delta == null ? null : delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (object == null) return;
					if (start) meta.setTransformationInterpolationStartDelta((Integer) object);
					else meta.setTransformationInterpolationDuration((int) NumberUtils.ticksFrom((Timespan) object));
				}
				case RESET -> {
					if (start) meta.setTransformationInterpolationStartDelta(0);
					else meta.setTransformationInterpolationDuration(0);
				}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "transformation interpolation " + (start ? "start" : "duration");
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

}
