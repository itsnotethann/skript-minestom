package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.events.minestom.CustomConnectEvent;
import ch.njol.skript.events.wrapper.CustomConnectWrapper;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;

public class CondIsOnline extends PropertyCondition<Player> {

	static {
		register(CondIsOnline.class, "online", "players");
	}

	@Override
	public boolean check(Event event) {
		Player customConnectPlayer = null;
		Boolean kicked = null;
		if (event instanceof CustomConnectWrapper wrapper) {
			CustomConnectEvent e = wrapper.getEvent();
			customConnectPlayer = e.getPlayer();
			kicked = e.isKicked();
		}
		for (Player player : getExpr().getArray(event)) {
			boolean online = player.isOnline();
			if (customConnectPlayer != null && customConnectPlayer.equals(player)) online = !kicked;
			if (!online) return isNegated();
		}
		return !isNegated();
	}

	@Override
	public boolean check(Player value) {
		throw new IllegalStateException("CondIsOnline#check(Player) should not be running");
	}

	@Override
	protected String getPropertyName() {
		return "online";
	}

}
