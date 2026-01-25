package org.bukkit;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitSchedulerImpl;
import org.bukkit.scheduler.DefaultTicker;
import org.bukkit.scheduler.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.logging.*;

public class Bukkit {
	private static final Thread primaryThread = Thread.currentThread();
	private static final PluginManager pluginManager = new SimplePluginManager();
	private static final Logger logger = Logger.getLogger("Bukkit");
	private static BukkitScheduler scheduler = null;
	private static ServicesManager servicesManager = null;
	private static Ticker ticker = new DefaultTicker();

	static {
		fixLoggerFormatting();
	}

	public static PluginManager getPluginManager() {
		return pluginManager;
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

	private static void fixLoggerFormatting() {
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		for (Handler handler : rootLogger.getHandlers()) {
			handler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return String.format("[%1$tT %2$s]: %3$s%n",
						record.getMillis(),
						record.getLevel().getName(),
						record.getMessage());
				}
			});
		}
	}
}
