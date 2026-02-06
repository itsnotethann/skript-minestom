package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Game Mode")
@Description("The game mode of a player.")
@Examples("set gamemode of player to creative")
public class ExprGameMode extends SimplePropertyExpression<Player, GameMode> {

	static {
		register(ExprGameMode.class, GameMode.class, "game[ ]mode", "players");
	}

	@Override
	public @Nullable GameMode convert(Player from) {
		return from.getGameMode();
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(GameMode.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		if (delta[0] == null) return;
		GameMode gameMode = (GameMode) delta[0];
		for (Player p : getExpr().getArray(event)) {
			p.setGameMode(gameMode);
		}
	}

	@Override
	protected String getPropertyName() {
		return "gamemode";
	}

	@Override
	public Class<? extends GameMode> getReturnType() {
		return GameMode.class;
	}

}
