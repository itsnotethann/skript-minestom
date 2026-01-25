package ch.njol.skript.events.wrapper;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class EventWrapper<E extends net.minestom.server.event.Event> extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	protected final E event;

	public EventWrapper(E event) {
		this.event = event;
	}

	public E getEvent() {
		return event;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
