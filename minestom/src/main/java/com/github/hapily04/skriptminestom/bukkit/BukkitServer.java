package com.github.hapily04.skriptminestom.bukkit;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public class BukkitServer implements Server {

	private static final ConsoleCommandSenderImpl CONSOLE_SENDER = new ConsoleCommandSenderImpl();

	@Override
	public ConsoleCommandSender getConsoleSender() {
		return CONSOLE_SENDER;
	}

}
