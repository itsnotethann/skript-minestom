package ch.njol.skript.events.click;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.EffCancelEvent;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.CancellableEvent;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Tracks click events to remove extraneous events for one player click.
 */
public class ClickEventTracker {

	/**
	 * @param event The actual event that is tracked.
	 * @param hand  Hand used in event.
	 */
	private record TrackedEvent(Event event, @SuppressWarnings("unused") PlayerHand hand) {

			private TrackedEvent(Event event, PlayerHand hand) {
				this.event = event;
				this.hand = hand;
			}

		}

	/**
	 * First events by players during this tick. They're stored by their UUIDs.
	 * This map is cleared once per tick.
	 */
	final Map<UUID, TrackedEvent> firstEvents;

	/**
	 * Events that have been cancelled with {@link EffCancelEvent}.
	 */
	private final Set<Event> modifiedEvents;

	public ClickEventTracker() {
		this.firstEvents = new HashMap<>();
		this.modifiedEvents = new HashSet<>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Skript.getInstance(),
			() -> {
				firstEvents.clear();
				modifiedEvents.clear();
			}, 1, 1);
	}

	/**
	 * Processes a click event from a player.
	 * @param player Player who caused it.
	 * @param event The event.
	 * @param hand Slot associated with the event.
	 * @return If the event should be passed to scripts.
	 */
	public boolean checkEvent(Player player, Event event, PlayerHand hand) {
		UUID uuid = player.getUuid();
		TrackedEvent first = firstEvents.get(uuid);
		if (first != null && first.event != event) { // We've checked an event before, and it is not this one
			if (!modifiedEvents.contains(first.event)) {
				// Do not modify cancellation status of event, Skript did not touch it
				// This avoids issues like #2389
				return false;
			}
			
			if (event instanceof CancellableEvent cancellable) cancellable.setCancelled(isCancelled(first.event));
			return false;
		} else { // Remember and run this
			firstEvents.put(uuid, new TrackedEvent(event, hand));
			return true;
		}
	}

	/**
	 * Records that given event was cancelled or uncancelled.
	 * @param event The event.
	 */
	public void eventModified(Event event) {
		modifiedEvents.add(event);
	}

	private boolean isCancelled(Event event) {
		if (!(event instanceof CancellableEvent cancellable)) return false;
		return cancellable.isCancelled();
	}
}