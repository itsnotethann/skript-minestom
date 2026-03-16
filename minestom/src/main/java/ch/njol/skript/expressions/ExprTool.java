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
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.inventory.EquipmentHandler;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Held Item")
@Description("The held item (tool) of an entity.")
@Examples("set tool of player to diamond sword")
public class ExprTool extends PropertyExpression<EquipmentHandler, Slot> {

	static {
		Skript.registerExpression(ExprTool.class, Slot.class, ExpressionType.PROPERTY,
			"[the] (tool|held item|weapon) [of %equipmenthandlers%]",
			"%equipmenthandlers%'[s] (tool|held item|weapon)",
			"[the] off[ ]hand (tool|item) [of %equipmenthandlers%]",
			"%equipmenthandlers%'[s] off[ ]hand (tool|item)"
		);
	}

	private EquipmentSlot handSlot;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends EquipmentHandler>) expressions[0]);
		handSlot = matchedPattern >= 2 ? EquipmentSlot.OFF_HAND : EquipmentSlot.MAIN_HAND;
		return true;
	}

	@Override
	protected Slot[] get(Event event, EquipmentHandler[] source) {
		List<Slot> slots = new ArrayList<>();
		for (EquipmentHandler handler : source) {
			slots.add(new Slot(handler.getEquipment(handSlot), handler, handSlot));
		}
		return slots.toArray(new Slot[0]);
	}

	@Override
	public Class<? extends Slot> getReturnType() {
		return Slot.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String toString = "";
		if (handSlot == EquipmentSlot.OFF_HAND) toString += "off hand ";
		toString += "tool of " + getExpr().toString(event, debug);
		return toString;
	}

}
