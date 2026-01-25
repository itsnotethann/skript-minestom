package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprItemWithLore extends SimpleExpression<Item> {

	static {
		Skript.registerExpression(ExprItemWithLore.class, Item.class, ExpressionType.COMBINED, "%item% with lore %components/strings%");
	}

	private Expression<Item> itemExpr;
	private Expression<Object> loreExpr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		itemExpr = (Expression<Item>) expressions[0];
		loreExpr = (Expression<Object>) expressions[1];
		return true;
	}

	@Override
	protected @Nullable Item[] get(Event event) {
		Item item = itemExpr.getSingle(event);
		if (item == null) return new Item[0];
		Object[] lore = loreExpr.getArray(event);
		int length = lore.length;
		Component[] components = new Component[length];
		for (int i = 0; i < length; i++) {
			Object o = lore[i];
			if (o instanceof Component component) components[i] = component;
			else components[i] = Component.text((String) o);
		}
		item.modify(i -> i.withLore(components));
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
		return itemExpr.toString(event, debug) + " with lore " + loreExpr.toString(event, debug);
	}

}
