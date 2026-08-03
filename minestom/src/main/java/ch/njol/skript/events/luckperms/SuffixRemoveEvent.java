package ch.njol.skript.events.luckperms;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class SuffixRemoveEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(SuffixRemoveEvent.class, Player.class, SuffixRemoveEvent::getReceiver));
		EventValues.registerEventValue(EventValue.builder(SuffixRemoveEvent.class, String.class)
			.patterns("suffix")
			.getter(SuffixRemoveEvent::getSuffix)
			.build());
	}

	private final Player receiver;
	private final String suffix;

	public SuffixRemoveEvent(Player receiver, String suffix) {
		this.receiver = receiver;
		this.suffix = suffix;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
