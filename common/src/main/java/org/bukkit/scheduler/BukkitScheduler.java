package org.bukkit.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface BukkitScheduler {

	void tick();

	BukkitTask runTaskAsynchronously(Plugin plugin, Runnable runnable);

	int runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay);

	int scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay);

	int runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long duration);

	int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long duration);

	boolean isQueued(int taskID);

	boolean isCurrentlyRunning(int taskID);

	void cancelTask(int taskID);

	<T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task);
}