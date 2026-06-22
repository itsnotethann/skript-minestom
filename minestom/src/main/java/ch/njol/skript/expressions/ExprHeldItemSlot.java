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


@Name("Held Item Slot")
@Description("The hotbar slot index a player is holding.")
@Examples("set held item slot of player to 0")
public class ExprHeldItemSlot extends SimplePropertyExpression<Player, Integer> {

	static {
		register(ExprHeldItemSlot.class, Integer.class, "held [item] slot", "players");
	}

	@Override
	public @Nullable Integer convert(Player from) {
		return (int) from.getHeldSlot();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Integer.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Player[] players = getExpr().getArray(event);
		Integer slot = delta == null ? null : (Integer) delta[0];
		if (slot == null || slot < 0 || slot > 8) return;
		for (Player player : players) {
			player.setHeldItemSlot(slot.byteValue());
		}
	}

	@Override
	protected String getPropertyName() {
		return "held item slot";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
