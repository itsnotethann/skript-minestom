package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Enchantment;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.hapily04.skriptminestom.registration.MinestomClasses.ITEM_CHANGER;

@Name("Enchantments")
@Description("The enchantments of an item.")
@Examples("set enchantments of player's tool to protection 4 and unbreaking 3")
public class ExprEnchants extends PropertyExpression<Item, Enchantment> {

	static {
		register(ExprEnchants.class, Enchantment.class, "enchant[(s|ment[s])]", "items");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Item>) expressions[0]);
		return true;
	}

	@Override
	protected Enchantment[] get(Event event, Item[] source) {
		List<Enchantment> enchants = new ArrayList<>();
		for (Item item : source) {
			enchants.addAll(List.of(Enchantment.getEnchants(item)));
		}
		return enchants.toArray(new Enchantment[0]);
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case DELETE, SET, REMOVE, ADD, RESET -> CollectionUtils.array(Enchantment[].class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Item[] items = getExpr().getArray(event);
		Enchantment[] enchants = delta == null ? new Enchantment[0] : Arrays.copyOf(delta, delta.length, Enchantment[].class);
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) Changer.ChangerUtils.change(ITEM_CHANGER, items, enchants, mode);
		for (Item item : items) {
			switch (mode) {
				case SET -> Enchantment.set(item, enchants);
				case DELETE, RESET -> Enchantment.set(item);
			}
		}
	}

	@Override
	public Class<? extends Enchantment> getReturnType() {
		return Enchantment.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "enchantments of " + getExpr().toString(event, debug);
	}

}
