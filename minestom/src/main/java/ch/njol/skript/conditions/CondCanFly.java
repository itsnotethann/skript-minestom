package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import net.minestom.server.entity.Player;

@Name("Can Fly")
@Description("Whether a player is allowed to fly.")
@Example("player can fly")
public class CondCanFly extends PropertyCondition<Player> {

	static {
		register(CondCanFly.class, PropertyType.CAN, "fly", "players");
	}

	@Override
	public boolean check(Player player) {
		return player.isAllowFlying();
	}

	@Override
	protected PropertyType getPropertyType() {
		return PropertyType.CAN;
	}

	@Override
	protected String getPropertyName() {
		return "fly";
	}

}
