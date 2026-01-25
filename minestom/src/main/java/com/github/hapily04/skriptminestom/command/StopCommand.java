package com.github.hapily04.skriptminestom.command;

import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
        setCondition((sender, commandString) -> LuckPermsPlayer.hasPermission(sender, "skript.stop"));
        setDefaultExecutor((sender, context) -> {
            MinecraftServer.stopCleanly();
        });
    }

}
