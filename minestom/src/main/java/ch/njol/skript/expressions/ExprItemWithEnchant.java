package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Enchantment;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprItemWithEnchant extends SimpleExpression<Item> {

	static {
		Skript.registerExpression(ExprItemWithEnchant.class, Item.class, ExpressionType.COMBINED,
			"%item% (of|with) [enchant[(s|ment[s])]] %enchantments%");
	}

	private Expression<Item> item;
	private Expression<Enchantment> enchants;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		item = (Expression<Item>) expressions[0];
		enchants = (Expression<Enchantment>) expressions[1];
		return true;
	}

	@Override
	protected @Nullable Item[] get(Event event) {
		Item item = this.item.getSingle(event);
		if (item == null) return new Item[0];
		item = item.copy();
		Enchantment[] enchants = this.enchants.getArray(event);
		Enchantment.add(item, false, enchants);
		return new Item[]{item};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Item> getReturnType() {
		return Item.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return item.toString(event, debug) + " with enchants " + enchants.toString(event, debug);
	}

}
