package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Make Fly")
@Description("Forces a player to start/stop flying.")
@Example("make player fly")
@Example("force all players to stop flying")
public class EffMakeFly extends Effect {

	static {
		Skript.registerEffect(EffMakeFly.class, "force %players% to [(start|1¦stop)] fly[ing]",
			"make %players% (start|1¦stop) flying",
			"make %players% fly");
	}

	@SuppressWarnings("null")
	private Expression<Player> players;
	private boolean flying;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		flying = parseResult.mark != 1;
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Player player : players.getArray(e)) {
			player.setAllowFlying(flying);
			player.setFlying(flying);
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + players.toString(e, debug) + (flying ? " start " : " stop ") + "flying";
	}

}
