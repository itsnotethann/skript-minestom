package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.EntityAttackWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class EvtAttack extends SkriptEvent {

	static {
		Skript.registerEvent("Entity Attack", EvtAttack.class, EntityAttackWrapper.class,
			"(%-entitytypes%|entity) attack [on %-entitytypes%]");
		EventValues.registerEventValue(EventValue.builder(EntityAttackWrapper.class, Entity.class)
			.patterns("attacker")
			.getter(from -> from.getEvent().getEntity())
			.build());
		EventValues.registerEventValue(EventValue.builder(EntityAttackWrapper.class, Entity.class)
			.patterns("victim")
			.getter(from -> from.getEvent().getTarget())
			.build());
	}

	@Nullable
	private Literal<EntityType> attackerTypes;
	@Nullable
	private Literal<EntityType> victimTypes;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		attackerTypes = (Literal<EntityType>) args[0];
		victimTypes = (Literal<EntityType>) args[1];
		return true;
	}

	@Override
	public boolean check(Event event) {
		EntityAttackEvent e = ((EntityAttackWrapper) event).getEvent();
		boolean attackerCheck = check(attackerTypes, e.getEntity().getEntityType());
		boolean victimCheck = check(victimTypes, e.getTarget().getEntityType());
		return attackerCheck && victimCheck;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (attackerTypes != null ? attackerTypes.toString(event, debug) : "entity") + " attack"
			+ (victimTypes != null ? " of " + victimTypes.toString(event, debug) : "");
	}

	private boolean check(@Nullable Literal<EntityType> entityTypes, EntityType type) {
		if (entityTypes == null) return true;
		for (EntityType t : entityTypes.getAll()) {
			if (t.equals(type)) {
				return true;
			}
		}
		return false;
	}

}
