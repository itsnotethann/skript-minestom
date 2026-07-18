package org.bukkit.event;

import java.util.ArrayList;
import java.util.List;

public class HandlerList {
	private static final List<HandlerList> allLists = new ArrayList<>();

	private final List<RegisteredListener> listeners = new ArrayList<>();

	public HandlerList() {
		synchronized (allLists) {
			allLists.add(this);
		}
	}

	public synchronized void register(RegisteredListener handler) {
		listeners.add(handler);
	}

	public synchronized void unregister(Listener listener) {
		listeners.removeIf((registeredListener) -> registeredListener.getListener() == listener);
	}

	public static void unregisterAll(Listener listener) {
		List<HandlerList> snapshot;
		synchronized (allLists) {
			snapshot = List.copyOf(allLists);
		}
		for (HandlerList handlerList : snapshot) {
			handlerList.unregister(listener);
		}
	}

	public synchronized List<RegisteredListener> getRegisteredListeners() {
		return List.copyOf(listeners);
	}
}
