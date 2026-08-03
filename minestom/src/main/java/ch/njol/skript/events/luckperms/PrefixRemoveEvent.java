package ch.njol.skript.events.luckperms;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PrefixRemoveEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(PrefixRemoveEvent.class, Player.class, PrefixRemoveEvent::getReceiver));
		EventValues.registerEventValue(EventValue.builder(PrefixRemoveEvent.class, String.class)
			.patterns("prefix")
			.getter(PrefixRemoveEvent::getPrefix)
			.build());
	}

	private final Player receiver;
	private final String prefix;

	public PrefixRemoveEvent(Player receiver, String prefix) {
		this.receiver = receiver;
		this.prefix = prefix;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
