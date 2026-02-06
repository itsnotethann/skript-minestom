package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Amount of Items")
@Description("Returns a certain amount of an item.")
@Examples("give 5 of player's tool to player")
@SuppressWarnings("NullableProblems")
public class ExprXOfItem extends PropertyExpression<Item, Item> {

	static {
		Skript.registerExpression(ExprXOfItem.class, Item.class, ExpressionType.COMBINED, "%integer% of %items%");
	}

	private Expression<Integer> amount;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		amount = (Expression<Integer>) expressions[0];
		setExpr((Expression<? extends Item>) expressions[1]);
		return true;
	}

	@Override
	protected Item[] get(Event event, Item[] source) {
		Integer amount = this.amount.getSingle(event);
		if (amount == null) return new Item[0];
		List<Item> items = new ArrayList<>();
		for (Item item : source) {
			Item newItem = item.copy();
			newItem.modify(i -> i.withAmount(amount));
			items.add(newItem);
		}
		return items.toArray(new Item[0]);
	}

	@Override
	public boolean isSingle() {
		return getExpr().isSingle();
	}

	@Override
	public Class<? extends Item> getReturnType() {
		return Item.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return amount.toString(event, debug) + " of " + getExpr().toString(event, debug);
	}

}
