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


@Name("Permission Level")
@Description("""
	A player's permission level. This is only used to tell the client what "permissions" it has.
	This is used for whether the player can use the gamemode switcher, operator creative tab, entity selector in commands (@s, @a, etc.), etc.""")
@Examples("set permission level of player to 4")
public class ExprPermissionLevel extends SimplePropertyExpression<Player, Integer> {

	static {
		register(ExprPermissionLevel.class, Integer.class, "permission level", "players");
	}

	@Override
	public @Nullable Integer convert(Player from) {
		return from.getPermissionLevel();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Player[] players = getExpr().getArray(event);
		Integer permissionLevel = delta == null ? null : (Integer) delta[0];
		for (Player player : players) {
			if (mode == Changer.ChangeMode.RESET) {
				player.setPermissionLevel(0);
				continue;
			}
			if (permissionLevel == null || permissionLevel < 0 || permissionLevel > 4) return;
			player.setPermissionLevel(permissionLevel);
		}
	}

	@Override
	protected String getPropertyName() {
		return "permission level";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
