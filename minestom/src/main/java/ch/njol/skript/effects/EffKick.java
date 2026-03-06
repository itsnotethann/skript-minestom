package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.events.minestom.CustomConnectEvent;
import ch.njol.skript.events.wrapper.CustomConnectWrapper;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffKick extends Effect {

	static {
		Skript.registerEffect(EffKick.class, "kick %players% [due to %-component%]");
	}

	private Expression<Player> players;
	@Nullable
	private Expression<Component> message;
	private boolean connectEvent;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		players = (Expression<Player>) expressions[0];
		message = (Expression<Component>) expressions[1];
		connectEvent = getParser().isCurrentEvent(CustomConnectWrapper.class);
		return true;
	}

	@Override
	protected void execute(Event event) {
		Component message = this.message == null ? Component.empty() : this.message.getSingle(event);
		if (message == null) return;
		for (Player player : players.getArray(event)) {
			if (connectEvent) {
				CustomConnectEvent e = ((CustomConnectWrapper) event).getEvent();
				if (e.getPlayer().equals(player)) {
					e.kick(message);
					continue;
				}
			}
			player.kick(message);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "kick " + players.toString(event, debug) + (message == null ? "" : message.toString(event, debug));
	}

}
