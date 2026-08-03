package ch.njol.skript.events.luckperms;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class GroupRemoveEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(GroupRemoveEvent.class, Player.class, GroupRemoveEvent::getReceiver));
		EventValues.registerEventValue(EventValue.builder(GroupRemoveEvent.class, String.class)
			.patterns("group")
			.getter(GroupRemoveEvent::getGroup)
			.build());
	}

	private final Player receiver;
	private final String group;

	public GroupRemoveEvent(Player receiver, String group) {
		this.receiver = receiver;
		this.group = group;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
