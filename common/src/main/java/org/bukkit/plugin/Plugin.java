package org.bukkit.plugin;

import java.io.File;

public interface Plugin {
	File getDataFolder();
	PluginDescriptionFile getDescription();
	void setEnabled(boolean enabled);
	boolean isEnabled();
	String getName();

	void onEnable();
	void onDisable();
}
