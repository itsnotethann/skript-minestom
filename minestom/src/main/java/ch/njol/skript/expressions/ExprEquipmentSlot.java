package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.EquipmentHandler;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minestom.server.utils.inventory.PlayerInventoryUtils.OFFHAND_SLOT;

public class ExprEquipmentSlot extends PropertyExpression<EquipmentHandler, Slot> {

	static {
		Skript.registerExpression(ExprEquipmentSlot.class, Slot.class, ExpressionType.PROPERTY,
			"[the] %equipmentslots% [slot[s]] [of %equipmenthandlers%]",
			"%equipmenthandlers%'[s] %equipmentslots% [slot[s]]"
		);
	}

	private Expression<EquipmentSlot> slot;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		int slotIndex = 0;
		int handlerIndex = 1;
		if (matchedPattern == 1) {
			slotIndex = 1;
			handlerIndex = 0;
		}
		slot = (Expression<EquipmentSlot>) expressions[slotIndex];
		setExpr((Expression<? extends EquipmentHandler>) expressions[handlerIndex]);
		return true;
	}

	@Override
	protected Slot[] get(Event event, EquipmentHandler[] source) {
		List<Slot> slots = new ArrayList<>();
		EquipmentSlot[] equipmentSlots = slot.getArray(event);
		for (EquipmentHandler handler : source) {
			for (EquipmentSlot slot : equipmentSlots) {
				if (handler instanceof Player player && getSlotId(slot, player.getHeldSlot()) < 0) continue;
				slots.add(new Slot(handler.getEquipment(slot), handler, slot));
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
		return slot.toString(event, debug) + " slot of " + getExpr().toString(event, debug);
	}

	// From PlayerInventory class
	private int getSlotId(EquipmentSlot slot, byte heldSlot) {
		return switch (slot) {
			case MAIN_HAND -> heldSlot;
			case OFF_HAND -> OFFHAND_SLOT;
			default -> slot.armorSlot();
		};
	}

}
