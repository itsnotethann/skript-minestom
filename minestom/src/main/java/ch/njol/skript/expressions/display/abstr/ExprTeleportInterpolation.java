package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;


@Name("Teleport Interpolation Duration")
@Description("The teleport interpolation duration of a display entity.")
@Examples("set teleport interpolation duration of {_entity} to 1 second")
public class ExprTeleportInterpolation extends SimplePropertyExpression<Entity, Timespan> {

	static {
		register(ExprTeleportInterpolation.class, Timespan.class,
			"teleport [interpolation] duration", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Timespan convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return NumberUtils.timespanFrom(meta.getPosRotInterpolationDuration());
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Timespan.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Timespan timespan = delta == null ? null : (Timespan) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (timespan == null) return;
					meta.setPosRotInterpolationDuration((int) NumberUtils.ticksFrom(timespan));
				}
				case RESET -> meta.setPosRotInterpolationDuration(0);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "teleport interpolation duration";
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

}
