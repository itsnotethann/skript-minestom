package org.bukkit;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;

public interface Server {

	default PluginManager getPluginManager() {
		return Bukkit.getPluginManager();
	}

	ConsoleCommandSender getConsoleSender();

	Collection<Player> getOnlinePlayers();

	boolean getOnlineMode();

	String getVersion();

	String getName();

}
