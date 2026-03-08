package com.github.hapily04.skriptminestom.bukkit;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class ConsoleCommandSenderImpl implements ConsoleCommandSender {

	@Override
	public void sendMessage(@NotNull String message) {
		sendMessage(UUID.randomUUID(), message);
	}

	@Override
	public void sendMessage(@NonNull @NotNull String... messages) {
		sendMessage(UUID.randomUUID(), messages);
	}

	@Override
	public void sendMessage(@Nullable UUID sender, @NotNull String message) {
		MinecraftServer.getCommandManager().getConsoleSender().sendMessage(message);
	}

	@Override
	public void sendMessage(@Nullable UUID sender, @NonNull @NotNull String... messages) {
		ConsoleSender consoleSender = MinecraftServer.getCommandManager().getConsoleSender();
		for (String message : messages) {
			consoleSender.sendMessage(message);
		}
	}

	@Override
	public @NotNull Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public @NotNull String getName() {
		return "console";
	}

	@Override
	public boolean isPermissionSet(@NotNull String name) {
		return true;
	}

	@Override
	public boolean isPermissionSet(@NotNull Permission perm) {
		return true;
	}

	@Override
	public boolean hasPermission(@NotNull String name) {
		return true;
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {}

}
