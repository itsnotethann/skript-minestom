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

@Name("Clear Resource Pack")
@Description("Clears all resource packs from the given players.")
@Examples("clear player's resource pack")
public class EffClearPack extends Effect {

	static {
		Skript.registerEffect(EffClearPack.class, "clear %players%'[s] [resource] pack[s]");
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
			player.clearResourcePacks();
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "clear " + players.toString(event, debug) + " resource pack[s]";
	}

}
