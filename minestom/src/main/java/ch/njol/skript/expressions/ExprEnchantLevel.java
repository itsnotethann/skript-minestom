package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Enchantment;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Name("Enchantment Level")
@Description("The level of an enchantment on an item.")
@Examples("set sharpness enchantment level of player's tool to 5")
public class ExprEnchantLevel extends PropertyExpression<Item, Integer> {

	static {
		Skript.registerExpression(ExprEnchantLevel.class, Integer.class, ExpressionType.PROPERTY,
			"[the] [enchant[ment]] level[s] of %enchantments% (on|of) %items%",
			"[the] %enchantments% [enchant[ment]] level[s] (on|of) %items%",
			"%items%'[s] %enchantments% [enchant[ment]] level[s]",
			"%items%'[s] [enchant[ment]] level[s] of %enchantments%");
	}

	private Expression<Enchantment> enchants;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		int i = matchedPattern < 2 ? 1 : 0;
		setExpr((Expression<Item>) exprs[i]);
		enchants = (Expression<Enchantment>) exprs[i ^ 1];
		if (enchants instanceof Literal<Enchantment> literal) {
			for (Enchantment enchantment : literal.getAll()) {
				if (enchantment.level() != -1) {
					Skript.error("Error providing '" + enchantment.enchantment().key().asMinimalString() + "' as an enchantment: " +
						"You can't provide enchantments with levels attached to them.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected Integer[] get(Event event, Item[] source) {
		Enchantment[] enchantments = enchants.getArray(event);
		List<Integer> levels = new ArrayList<>();
		for (Item item : source) {
			for (Enchantment enchantment : enchantments) {
				levels.add(Enchantment.getLevel(item, enchantment.enchantment()));
			}
		}
		return levels.toArray(new Integer[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
		Item[] items = getExpr().getArray(e);
		Enchantment[] enchantments = enchants.getArray(e);
		int changeValue = ((Number) delta[0]).intValue();

		for (Item item : items) {
			for (Enchantment enchantment : enchantments) {
				int oldLevel = Enchantment.getLevel(item, enchantment.enchantment());
				int newItemLevel;
				switch (mode) {
					case ADD:
						newItemLevel = oldLevel + changeValue;
						break;
					case REMOVE:
						newItemLevel = oldLevel - changeValue;
						break;
					case SET:
						newItemLevel = changeValue;
						break;
					default:
						assert false;
						return;
				}

				if (newItemLevel <= 0) {
					Enchantment.remove(item, enchantment);
				} else {
					Enchantment.add(item, true, new Enchantment(enchantment.enchantment(), newItemLevel));
				}
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "the level of " + enchants.toString(e, debug) + " of " + getExpr().toString(e, debug);
	}

}
