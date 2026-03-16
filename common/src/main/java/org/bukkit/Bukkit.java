package org.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitSchedulerImpl;
import org.bukkit.scheduler.DefaultTicker;
import org.bukkit.scheduler.Ticker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

public class Bukkit {
	private static final Thread primaryThread = Thread.currentThread();
	private static final PluginManager pluginManager = new SimplePluginManager();
	private static final Logger logger = LoggerFactory.getLogger(Bukkit.class);
	private static BukkitScheduler scheduler = null;
	private static ServicesManager servicesManager = null;
	private static Ticker ticker = new DefaultTicker();
	private static Server server = null;

	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	public static Server getServer() {
		return server;
	}

	public static void setServer(Server server) {
		if (server == null) throw new IllegalStateException("Server has already been set!");
		Bukkit.server = server;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static @NotNull BukkitScheduler getScheduler() {
		if (scheduler == null)
			scheduler = new BukkitSchedulerImpl();

		return scheduler;
	}

	public static @NotNull ServicesManager getServicesManager() {
		if (servicesManager == null)
			servicesManager = new SimpleServicesManager();
		return servicesManager;
	}

	public static boolean getOnlineMode() {
		return server.getOnlineMode();
	}

	public static String getVersion() {
		return server.getVersion();
	}

	public static String getName() {
		return server.getName();
	}

	public static Collection<Player> getOnlinePlayers() {
		return server.getOnlinePlayers();
	}

	public static Player getPlayer(UUID uuid) {
		return server.getPlayer(uuid);
	}

	public static boolean isPrimaryThread() {
		return Thread.currentThread().equals(primaryThread);
	}

	public static Thread getPrimaryThread() {
		return primaryThread;
	}

	public static Ticker getTicker() {
		return ticker;
	}

	public static void setTicker(Ticker ticker) {
		Bukkit.ticker = ticker;
	}
}
