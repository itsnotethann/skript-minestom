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


@Name("Listed")
@Description("Whether a player is listed in the tab list.")
@Examples("set listed property of player to false")
public class ExprListed extends SimplePropertyExpression<Player, Boolean> {

	static {
		register(ExprListed.class, Boolean.class, "listed [property]", "players");
	}

	@Override
	public @Nullable Boolean convert(Player from) {
		return from.hasPhysics();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Boolean.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Boolean state = delta == null ? null : (Boolean) delta[0];
		for (Player p : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (state == null) return;
					p.setListed(state);
				}
				case RESET -> p.setListed(true);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "listed property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
