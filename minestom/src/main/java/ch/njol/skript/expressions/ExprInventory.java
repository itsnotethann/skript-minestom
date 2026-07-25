package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Inventory")
@Description("The inventory of a player/slot.")
@Examples("clear inventory of player")
public class ExprInventory extends PropertyExpression<Object, AbstractInventory> {

	static {
		register(ExprInventory.class, AbstractInventory.class, "[open:(current|open)] inventory", "players/slots");
	}

	private boolean open = false;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		open = parseResult.hasTag("open");
		Expression<?> expr = expressions[0];
		if (!expr.getReturnType().equals(Player.class) && open) {
			Skript.error("Cannot use 'open' if the expression is not of type player.");
			return false;
		}
		setExpr(expr);
		return true;
	}

	@Override
	public Class<? extends AbstractInventory> getReturnType() {
		return AbstractInventory.class;
	}

	@Override
	protected AbstractInventory[] get(Event event, Object[] source) {
		List<AbstractInventory> inventories = new ArrayList<>();
		for (Object o : source) {
			if (o instanceof Player player) inventories.add(open ? player.getOpenInventory() : player.getInventory());
			else if (o instanceof Slot slot) {
				AbstractInventory container = slot.getContainer();
				if (container != null) inventories.add(container);
			}
		}
		return inventories.toArray(new AbstractInventory[0]);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (open ? "open " : "") + "inventory of " + getExpr().toString(event, debug);
	}

}
