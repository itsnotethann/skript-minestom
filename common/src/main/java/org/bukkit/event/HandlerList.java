package org.bukkit.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.plugin.SimplePluginManager.getHandlerList;

public class HandlerList {
	private final List<RegisteredListener> listeners = new ArrayList<>();

	public void register(RegisteredListener handler) {
		listeners.add(handler);
	}

	public void unregister(Listener listener) {
		listeners.removeIf((registeredListener) -> registeredListener.getListener() == listener);
	}

	public static void unregisterAll(Listener listener) {
		for (Method method : listener.getClass().getMethods()) {
			if (!method.isAnnotationPresent(EventHandler.class) || method.getParameterCount() != 1)
				continue;

			Class<?> event = method.getParameterTypes()[0];

			if (!Event.class.isAssignableFrom(event))
				continue;

			try {
				@SuppressWarnings("unchecked")
				HandlerList handlerList = getHandlerList((Class<? extends Event>) event);
				handlerList.unregister(listener);
			} catch (ReflectiveOperationException exception) {
				exception.printStackTrace();
			}
		}
	}

	public List<RegisteredListener> getRegisteredListeners() {
		return listeners;
	}
}
