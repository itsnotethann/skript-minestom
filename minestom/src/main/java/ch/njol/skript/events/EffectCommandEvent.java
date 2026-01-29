package ch.njol.skript.events;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EffectCommandEvent extends Event implements CancellableEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	static {
		EventValues.registerEventValue(EffectCommandEvent.class, Player.class, EffectCommandEvent::getExecutor, EventValues.TIME_NOW);
		EventValues.registerEventValue(EffectCommandEvent.class, Instance.class, event -> event.getExecutor().getInstance(), EventValues.TIME_NOW);
	}

	private final Player executor;
	private boolean cancelled = false;

	public EffectCommandEvent(Player executor) {
		this.executor = executor;
	}

	public Player getExecutor() {
		return executor;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
