package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.wrapper.EntityAttackWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class EvtAttack extends SkriptEvent {

	static {
		Skript.registerEvent("Entity Attack", EvtAttack.class, EntityAttackWrapper.class, "[%-entitytypes%] attack[ing]");
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
	private Literal<EntityType> types;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		types = (Literal<EntityType>) args[0];
		return true;
	}

	@Override
	public boolean check(Event event) {
		if (types == null) return true;
		EntityType toCheck = ((EntityAttackWrapper) event).getEvent().getEntity().getEntityType();
		EntityType[] types = this.types.getAll();
		for (EntityType type : types) {
			if (type.equals(toCheck)) return true;
		}
		return false;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (types != null ? types.toString(event, debug) + " " : "") + "attack";
	}

}
