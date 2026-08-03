package com.github.hapily04.skriptminestom.util;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import org.bukkit.permissions.Permissible;

public final class PermissionUtils {

	private PermissionUtils() {
	}

	public static boolean hasPermission(CommandSender sender, String permissionNode) {
		return sender instanceof ConsoleSender || (sender instanceof Permissible p && p.hasPermission(permissionNode));
	}

}
