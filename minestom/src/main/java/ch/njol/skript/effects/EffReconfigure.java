package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Reconfigure")
@Description("Puts the given players into the configuration phase, used when changing client-side settings such as resource packs.")
@Examples("reconfigure player")
public class EffReconfigure extends Effect {

	static {
		Skript.registerEffect(EffReconfigure.class, "reconfigure %players%");
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
			player.startConfigurationPhase();
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "reconfigure " + players.toString(event, debug);
	}

}
