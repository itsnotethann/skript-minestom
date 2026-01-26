package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.EquipmentSlot;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprAllEquipmentSlots extends SimpleExpression<EquipmentSlot> {

	private static final EquipmentSlot[] ARMOR_EQUIPMENT_SLOTS = EquipmentSlot.armors().toArray(new EquipmentSlot[0]);

	static {
		Skript.registerExpression(ExprAllEquipmentSlots.class, EquipmentSlot.class, ExpressionType.SIMPLE,
			"all [of] [the] (:equipment|armor) slots");
	}

	private boolean armor = false;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		armor = !parseResult.hasTag("equipment");
		return true;
	}

	@Override
	protected @Nullable EquipmentSlot[] get(Event event) {
		return armor ? ARMOR_EQUIPMENT_SLOTS : EquipmentSlot.values();
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends EquipmentSlot> getReturnType() {
		return EquipmentSlot.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "all " + (armor ? "armor" : "equipment") + " slots";
	}

}
