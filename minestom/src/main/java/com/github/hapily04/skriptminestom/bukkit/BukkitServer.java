package com.github.hapily04.skriptminestom.bukkit;

import net.minestom.server.Auth;
import net.minestom.server.Git;
import net.minestom.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BukkitServer implements Server {

	private static final ConsoleCommandSenderImpl CONSOLE_SENDER = new ConsoleCommandSenderImpl();
	private static final Player DUMMY_PLAYER = new Player();

	@Override
	public ConsoleCommandSender getConsoleSender() {
		return CONSOLE_SENDER;
	}

	@Override
	public Collection<Player> getOnlinePlayers() {
		return MinecraftServer.getConnectionManager().getOnlinePlayers().stream().map(_ -> DUMMY_PLAYER).toList();
	}

	@Override
	public boolean getOnlineMode() {
		return MinecraftServer.process().auth().getClass().equals(Auth.Online.class);
	}

	@Override
	public String getVersion() {
		return MinecraftServer.VERSION_NAME;
	}

	@Override
	public String getName() {
		return "Skript-Minestom (" + Git.version() + ")";
	}

}
