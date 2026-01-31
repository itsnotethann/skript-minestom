package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.scoreboard.TeamBuilder;
import org.bukkit.event.Event;

public class ExprGlowColorOverride extends SimplePropertyExpression<Entity, NamedTextColor> {

	static {
		register(ExprGlowColorOverride.class, NamedTextColor.class, "glow color override", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable NamedTextColor convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return NamedTextColor.namedColor(meta.getGlowColorOverride());
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(NamedTextColor.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		NamedTextColor color = delta == null ? null : (NamedTextColor) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (color == null) return;
					meta.setGlowColorOverride(color.value());
				}
				case RESET -> meta.setGlowColorOverride(-1);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "glow color override";
	}

	@Override
	public Class<? extends NamedTextColor> getReturnType() {
		return NamedTextColor.class;
	}

}
