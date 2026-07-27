package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Amount of Items")
@Description("Counts how many of a particular item are in a given inventory.")
@Example("message \"You have %number of diamond in player's inventory% diamond(s) in your inventory.\"")
@Since("2.0")
public class ExprAmountOfItems extends SimpleExpression<Long> {

	static {
		Skript.registerExpression(ExprAmountOfItems.class, Long.class, ExpressionType.PROPERTY,
			"[the] (amount|number) of %items% (in|of) %inventories%");
	}

	private Expression<Item> items;
	private Expression<AbstractInventory> inventories;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		items = (Expression<Item>) exprs[0];
		inventories = (Expression<AbstractInventory>) exprs[1];
		return true;
	}

	@Override
	protected Long[] get(Event e) {
		Item[] items = this.items.getArray(e);
		long amount = 0;
		for (AbstractInventory inventory : inventories.getArray(e)) {
			itemsLoop: for (ItemStack itemStack : inventory.getItemStacks()) {
				for (Item item : items) {
					if (itemStack.isSimilar(item.getItem())) {
						amount += itemStack.amount();
						continue itemsLoop;
					}
				}
			}
		}
		return new Long[]{amount};
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the number of " + items.toString(e, debug) + " in " + inventories.toString(e, debug);
	}

}
