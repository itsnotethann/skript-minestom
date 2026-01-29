package ch.njol.skript.events.wrapper.marker;

import ch.njol.skript.events.wrapper.EventWrapper;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Item;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.*;
import net.minestom.server.instance.Instance;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class MarkerRegistration {

	public static void register() {
		registerEventValue(EntityInstanceEventMarker.class, Instance.class, EntityInstanceEvent.class,
			EntityInstanceEvent::getInstance, EventValues.TIME_NOW);
		registerEventValue(EntityEventMarker.class, Entity.class, EntityEvent.class, EntityEvent::getEntity, EventValues.TIME_NOW);
		registerEventValue(PlayerEventMarker.class, Player.class, PlayerEvent.class, PlayerEvent::getPlayer, EventValues.TIME_NOW);
		registerEventValue(ItemEventMarker.class, Item.class, ItemEvent.class, itemEvent -> new Item(itemEvent.getItemStack()), EventValues.TIME_NOW);
	}

	private static <M, T, E extends Event> void registerEventValue(Class<M> markerClass, Class<T> returnType,
																   Class<E> expectedEventClass, Function<E, T> getter, int time) {
		EventValues.registerEventValueMarker(markerClass, returnType, new Getter<>() {
			@SuppressWarnings("unchecked")
			@Override
			public @Nullable T get(@NonNull M arg) {
				if (!(arg instanceof EventWrapper<?> wrapper)) return null;
				Event e = wrapper.getEvent();
				if (!expectedEventClass.isAssignableFrom(e.getClass())) return null;
				return getter.apply((E) e);
			}
		}, time);
	}

}
