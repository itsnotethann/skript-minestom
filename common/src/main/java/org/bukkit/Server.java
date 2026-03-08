package org.bukkit;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;

public interface Server {

	default PluginManager getPluginManager() {
		return Bukkit.getPluginManager();
	}

	ConsoleCommandSender getConsoleSender();

}
