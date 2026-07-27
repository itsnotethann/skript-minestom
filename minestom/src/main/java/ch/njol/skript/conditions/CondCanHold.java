package ch.njol.skript.conditions;

import ch.njol.skript.util.Item;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Can Hold")
@Description("Tests whether a player or a chest can hold the given item.")
@Example("block can hold 200 cobblestone")
@Example("player has enough space for 64 feather")
@Since("1.0")
public class CondCanHold extends Condition {

	static {
		Skript.registerCondition(CondCanHold.class,
			"%inventories% (can hold|ha(s|ve) [enough] space (for|to hold)) %items%",
			"%inventories% (can(no|')t hold|(ha(s|ve) not|ha(s|ve)n't|do[es]n't have) [enough] space (for|to hold)) %items%");
	}

	@SuppressWarnings("null")
	private Expression<AbstractInventory> invis;
	@SuppressWarnings("null")
	private Expression<Item> items;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		invis = (Expression<AbstractInventory>) exprs[0];
		items = (Expression<Item>) exprs[1];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return invis.check(e,
			invi ->
				items.check(e, t -> TransactionOption.DRY_RUN.fill(TransactionType.ADD, invi, t.getItem())),
			isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return PropertyCondition.toString(this, PropertyType.CAN, e, debug, invis,
			"hold " + items.toString(e, debug));
	}

}
