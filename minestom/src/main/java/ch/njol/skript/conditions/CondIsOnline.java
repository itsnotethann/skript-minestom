package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

@Name("Is Online")
@Description("Checks if a player is on the server.")
@Examples("if player is online:")
public class CondIsOnline extends PropertyCondition<Player> {

	static {
		register(CondIsOnline.class, "online", "players");
	}

	@Override
	public boolean check(Player p) {
		return p.isOnline();
	}

	@Override
	protected String getPropertyName() {
		return "online";
	}
}
