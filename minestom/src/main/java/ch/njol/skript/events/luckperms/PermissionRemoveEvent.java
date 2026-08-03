package ch.njol.skript.events.luckperms;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class PermissionRemoveEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(PermissionRemoveEvent.class, Player.class, PermissionRemoveEvent::getReceiver));
		EventValues.registerEventValue(EventValue.builder(PermissionRemoveEvent.class, String.class)
			.patterns("permission")
			.getter(PermissionRemoveEvent::getPermission)
			.build());
	}

	private final Player receiver;
	private final String permission;

	public PermissionRemoveEvent(Player receiver, String permission) {
		this.receiver = receiver;
		this.permission = permission;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
