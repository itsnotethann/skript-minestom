package ch.njol.skript.events.luckperms;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class GroupAddEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(GroupAddEvent.class, Player.class, GroupAddEvent::getReceiver));
		EventValues.registerEventValue(EventValue.builder(GroupAddEvent.class, String.class)
			.patterns("group")
			.getter(GroupAddEvent::getGroup)
			.build());
	}

	private final Player receiver;
	private final String group;

	public GroupAddEvent(Player receiver, String group) {
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
