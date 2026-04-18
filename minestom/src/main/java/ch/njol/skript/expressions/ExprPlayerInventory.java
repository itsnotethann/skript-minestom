package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Player Inventory")
@Description("The inventory of a player.")
@Examples("open inventory of player to player")
public class ExprPlayerInventory extends PropertyExpression<Player, AbstractInventory> {

	static {
		register(ExprPlayerInventory.class, AbstractInventory.class, "[open:(current|open)] inventory", "players");
	}

	private boolean open = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Player>) expressions[0]);
		open = parseResult.hasTag("open");
		return true;
	}

	@Override
	public Class<? extends AbstractInventory> getReturnType() {
		return AbstractInventory.class;
	}

	@Override
	protected AbstractInventory[] get(Event event, Player[] source) {
		AbstractInventory[] inventories = new AbstractInventory[source.length];
		for (int i = 0; i < source.length; i++) {
			Player player = source[i];
			inventories[i] = open ? player.getOpenInventory() : player.getInventory();
		}
		return inventories;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "open inventory of " + getExpr().toString(event, debug);
	}

}
