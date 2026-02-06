package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import net.minestom.server.inventory.AbstractInventory;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Inventory Slot")
@Description("A specific slot of an inventory.")
@Examples("set slot 0 of player's inventory to diamond")
public class ExprSlot extends PropertyExpression<AbstractInventory, Slot> {

	static {
		Skript.registerExpression(ExprSlot.class, Slot.class, ExpressionType.PROPERTY, "slot[s] %integers% of %inventories%");
	}

	private Expression<Integer> index;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		index = (Expression<Integer>) expressions[0];
		setExpr((Expression<? extends AbstractInventory>) expressions[1]);
		return true;
	}

	@Override
	protected Slot[] get(Event event, AbstractInventory[] source) {
		Integer[] slotIndexes = index.getArray(event);
		List<Slot> slots = new ArrayList<>();
		for (AbstractInventory inventory : source) {
			for (Integer slot : slotIndexes) {
				slots.add(new Slot(inventory.getItemStack(slot), inventory, slot));
			}
		}
		return slots.toArray(new Slot[0]);
	}

	@Override
	public Class<? extends Slot> getReturnType() {
		return Slot.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "slot " + index.toString(event, debug) + " of " + getExpr().toString(event, debug);
	}

}
