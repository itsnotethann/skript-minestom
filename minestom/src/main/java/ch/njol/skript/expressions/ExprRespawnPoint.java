package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Respawn Point")
@Description("The respawn point of a player.")
@Examples("set respawn point of player to player's location")
public class ExprRespawnPoint extends SimplePropertyExpression<Player, Point> {

	private static final Pos DEFAULT_SPAWNPOINT = new Pos(0, 0, 0, 0, 0);

	static {
		register(ExprRespawnPoint.class, Point.class, "respawn (point|position)", "players");
	}

	@Override
	public @Nullable Point convert(Player from) {
		return from.getRespawnPoint();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Point.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Point respawnPoint = delta == null ? null : (Point) delta[0];
		for (Player player : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (respawnPoint == null) return;
					player.setRespawnPoint(respawnPoint.asPos());
				}
				case RESET -> player.setRespawnPoint(DEFAULT_SPAWNPOINT);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "respawn point";
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

}
