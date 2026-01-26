package ch.njol.skript.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.njol.skript.util.Item;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Changer.ChangerUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Math2;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author joeuguce99
 */
@Name("Lore")
@Description("An item's lore.")
@Examples("set the 1st line of the item's lore to \"&lt;orange&gt;Excalibur 2.0\"")
@Since("2.1")
public class ExprLore extends SimpleExpression<Component> {

	static {
		Skript.registerExpression(ExprLore.class, Component.class, ExpressionType.PROPERTY,
			"[the] lore of %item%", "%item%'[s] lore",
			"[the] line %number% of [the] lore of %item%",
			"[the] line %number% of %item%'[s] lore",
			"[the] %number%(st|nd|rd|th) line of [the] lore of %item%",
			"[the] %number%(st|nd|rd|th) line of %item%'[s] lore");
	}

	@Nullable
	private Expression<Number> lineNumber;

	@SuppressWarnings("null")
	private Expression<Item> item;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		lineNumber = exprs.length > 1 ? (Expression<Number>) exprs[0] : null;
		item = (Expression<Item>) exprs[exprs.length - 1];
		return true;
	}

	@Override
	@Nullable
	protected Component[] get(final Event e) {
		final Item i = item.getSingle(e);
		final Number n = lineNumber != null ? lineNumber.getSingle(e) : null;
		if (n == null && lineNumber != null || i == null)
			return new Component[0];
		final ItemStack stack = i.getItem();
		if (stack.material() == Material.AIR)
			return new Component[0];
		List<Component> lore = stack.get(DataComponents.LORE);
		if (lore == null)
			return new Component[0];
		if (n == null)
			return lore.toArray(new Component[0]);
		final int l = n.intValue() - 1;
		if (l < 0 || l >= lore.size())
			return new Component[0];
		return new Component[]{lore.get(l)};
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		boolean acceptsMany = lineNumber == null;
		switch (mode) {
			case REMOVE:
			case REMOVE_ALL:
				if (!acceptsMany) return null;
			case DELETE:
				acceptsMany = false;
			case SET:
			case ADD:
				if (acceptsMany) return CollectionUtils.array(Component[].class);
				return CollectionUtils.array(Component.class);
			case RESET:
			default:
				return null;
		}
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		Item i = item.getSingle(e);

		Component[] componentD = delta == null ? new Component[0] : Arrays.copyOf(delta, delta.length, Component[].class);
		List<Component> componentDelta = List.of(componentD);

		// air is just nothing, it can't have a lore
		if (i == null)
			return;
		ItemStack item = i.getItem();
		if (item.material() == Material.AIR) return;

		Number lineNumber = this.lineNumber != null ? this.lineNumber.getSingle(e) : null;
		List<Component> itemLore = item.get(DataComponents.LORE);
		List<Component> lore = new ArrayList<>();
		if (itemLore != null) lore.addAll(itemLore);

		if (lineNumber == null) {
			// if the condition below is true, the pattern with the line %number% expression was used,
			// but the line number turned out to be null at runtime, meaning we should ignore it
			if (this.lineNumber != null) {
				return;
			}

			switch (mode) {
				case SET:
					lore = componentDelta;
					break;
				case ADD:
					lore.addAll(componentDelta);
					break;
				case DELETE:
					lore = new ArrayList<>();
					break;
				case REMOVE:
				case REMOVE_ALL:
					lore.removeAll(componentDelta);
					break;
				case RESET:
					assert false;
					return;
			}
		} else {
			// Note: line number is changed from one-indexed to zero-indexed here
			int lineNum = Math2.fit(0, lineNumber.intValue() - 1, 99); // TODO figure out the actual maximum

			// Fill in the empty lines above the line being set with empty strings (avoids index out of bounds)
			while (lore.size() <= lineNum)
				lore.add(Component.empty());
			switch (mode) {
				case SET:
					assert componentDelta != null;
					lore.set(lineNum, componentD[0]);
					break;
				case ADD:
					assert componentDelta != null;
					lore.set(lineNum, lore.get(lineNum).append(componentD[0]));
					break;
				case DELETE:
					lore.remove(lineNum);
					break;
				case REMOVE:
				case REMOVE_ALL:
				case RESET:
					assert false;
					return;
			}
		}

		List<Component> finalLore = lore;
		i.modify(_ -> item.withLore(finalLore), true);
	}

	@Override
	public boolean isSingle() {
		return lineNumber != null;
	}

	@Override
	public Class<? extends Component> getReturnType() {
		return Component.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (lineNumber != null ? "the line " + lineNumber.toString(e, debug) + " of " : "") + "the lore of " + item.toString(e, debug);
	}
}
