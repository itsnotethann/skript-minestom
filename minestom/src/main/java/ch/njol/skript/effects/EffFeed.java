package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Feed")
@Description("Fills the player's hunger bar.")
@Examples("feed all players")
public class EffFeed extends Effect {

	static {
		Skript.registerEffect(EffFeed.class, "feed %players%");
	}

	private Expression<Player> players;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		players = (Expression<Player>) expressions[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Player player : players.getArray(event)) {
			player.setFood(20);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "heal " + players.toString(event, debug);
	}

}
