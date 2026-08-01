package ch.njol.skript.events;

import ch.njol.skript.registrations.EventValues;
import net.minestom.server.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class UnknownCommandEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EventValue.simple(UnknownCommandEvent.class, CommandSender.class, UnknownCommandEvent::getSender));
		EventValues.registerEventValue(EventValue.builder(UnknownCommandEvent.class, String.class)
			.patterns("command")
			.getter(UnknownCommandEvent::getCommand)
			.build());
	}

	private final CommandSender sender;
	private final String command;

	public UnknownCommandEvent(CommandSender sender, String command) {
		this.sender = sender;
		this.command = command;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String getCommand() {
		return command;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
