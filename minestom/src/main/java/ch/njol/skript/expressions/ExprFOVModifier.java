package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Field of View (FOV) Modifier")
@Description("The modifier of the player's fov. A higher value will result in their screen appearing more zoomed in")
@Examples("set fov modifier of player to 0.5")
public class ExprFOVModifier extends SimplePropertyExpression<Player, Number> {

	static {
		register(ExprFOVModifier.class, Number.class, "(fov|field of view) modifier", "players");
	}

	@Override
	public @Nullable Number convert(Player from) {
		return from.getFieldViewModifier();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Number.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Number modifier = delta == null ? null : (Number) delta[0];
		Float mod = modifier == null ? null : modifier.floatValue();
		for (Player p : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (mod == null) return;
					p.setFieldViewModifier(mod);
				}
				case RESET -> p.setFieldViewModifier(0.1f);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "field of view modifier";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}

