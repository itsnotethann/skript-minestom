package com.github.hapily04.skriptminestom;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.events.EffectCommandEvent;
import ch.njol.skript.events.wrapper.EventWrapper;
import ch.njol.skript.events.wrapper.marker.MarkerRegistration;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.RedirectingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.Utils;
import ch.njol.skript.variables.Variables;
import com.github.hapily04.skriptminestom.command.SkriptCommand;
import com.github.hapily04.skriptminestom.command.StopCommand;
import com.github.hapily04.skriptminestom.luckperms.DummyContextProvider;
import com.github.hapily04.skriptminestom.luckperms.HoconConfigurationAdapter;
import com.github.hapily04.skriptminestom.luckperms.LuckPermsPlayer;
import com.github.hapily04.skriptminestom.registration.MinestomClasses;
import com.github.hapily04.skriptminestom.registration.MinestomFunctions;
import com.github.hapily04.skriptminestom.terminal.MinestomTerminal;
import com.github.hapily04.skriptminestom.util.FileUtils;
import com.github.hapily04.skriptminestom.util.PropertyUtils;
import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import me.lucko.spark.minestom.SparkMinestom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;

import static com.github.hapily04.skriptminestom.util.MessageUtils.BASIC_MINI_MESSAGE;
import static com.github.hapily04.skriptminestom.util.MessageUtils.SKRIPT_MINI_MESSAGE;
import static com.github.hapily04.skriptminestom.util.PropertyUtils.*;

public class SkriptMinestom {

	private static Properties properties;
	private static LuckPerms luckPerms;
	private static SparkMinestom spark;

	static void main() {
		properties = PropertyUtils.loadServerProperties();
		initMinestomProperties();
		MinecraftServer server = MinecraftServer.init(PropertyUtils.getAuth(properties));

		luckPerms = initLuckPerms();
		spark = initSpark();

		MinecraftServer.getConnectionManager().setPlayerProvider(
			(playerConnection, gameProfile) -> new LuckPermsPlayer(luckPerms, playerConnection, gameProfile));
		MinecraftServer.setBrandName(properties.getProperty(SERVER_BRAND));

		MinecraftServer.getCommandManager().register(new SkriptCommand(), new StopCommand());

		try {
			initSkript();
			initEffectCommands();
			scheduleShutdownTasks();
			server.start(properties.getProperty(ADDRESS_KEY), Integer.parseInt(properties.getProperty(PropertyUtils.PORT_KEY)));
			MinestomTerminal.start();
		} catch (Exception e) {
			System.err.println("An error occurred while initializing Skript-Minestom: " + e.getMessage());
			System.exit(1);
		}
	}

	private static void initMinestomProperties() {
		System.setProperty("minestom.send-light-after-block-placement-delay", "-1");
		System.setProperty("minestom.chunk-view-distance", properties.getProperty(CHUNK_VIEW_DISTANCE));
		System.setProperty("minestom.entity-view-distance", properties.getProperty(ENTITY_VIEW_DISTANCE));
		System.setProperty("minestom.dispatcher-threads", properties.getProperty(DISPATCHER_THREADS));
		System.setProperty("minestom.registry.unsafe-ops", "true");
	}

	private static LuckPerms initLuckPerms() {
		Path directory = new File(FileUtils.getServerDirectory(), "luckperms").toPath();
		return LuckPermsMinestom.builder(directory)
			.commandRegistry(CommandRegistry.minestom())
			.contextProvider(new DummyContextProvider())
			.configurationAdapter(plugin -> new MultiConfigurationAdapter(plugin,
				new EnvironmentVariableConfigAdapter(plugin),
				new HoconConfigurationAdapter(plugin)
			))
			.enable();
	}

	private static SparkMinestom initSpark() {
		Path directory = new File(FileUtils.getServerDirectory(), "spark").toPath();
		return SparkMinestom.builder(directory)
			.commands(true) // enables registration of Spark commands
			.permissionHandler((sender, a) -> LuckPermsPlayer.hasPermission(sender, "spark"))
			.enable();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void initSkript() throws URISyntaxException {
		//Bukkit.setTicker(tick -> MinecraftServer.getSchedulerManager().scheduleTask(tick, TaskSchedule.tick(1), TaskSchedule.tick(1))); // tick on minestom's thread
		Bukkit.getScheduler(); // initialize scheduler

		PluginManager pluginManager = Bukkit.getPluginManager();
		Plugin skript = pluginManager.loadPlugin(new File(Skript.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		Skript.onRegistration(() -> {
			MinestomClasses.register();
			MinestomFunctions.register();
			MarkerRegistration.register();
		});
		skript.setEnabled(true);
		skript.onEnable(); // have to manually initialize, addons are initialized on their own
		Skript.getAddonInstance().UNSAFE_setLanguageFileDirectory("minestomlang");

		// init events
		GlobalEventHandler geh = MinecraftServer.getGlobalEventHandler();
		for (SkriptEventInfo<?> eventInfo : Skript.getEvents()) {
			for (Class<? extends Event> bukkitEventClazz : eventInfo.events) {
				if (!EventWrapper.class.isAssignableFrom(bukkitEventClazz)) continue;
				Constructor<? extends Event> constructor = null;
				Class<? extends Event> eventType = null;
				for (Constructor<?> c :  bukkitEventClazz.getDeclaredConstructors()) {
					Class<?>[] parameters =  c.getParameterTypes();
					if (parameters.length != 1) continue;
					constructor = (Constructor<? extends Event>) c;
					eventType = (Class<? extends Event>) parameters[0];
				}
				if (constructor == null) continue;
				constructor.setAccessible(true);
				Constructor<? extends Event> finalConstructor = constructor;
				geh.addListener(eventType.asSubclass(net.minestom.server.event.Event.class), event -> {
					try {
						EventWrapper eventWrapper = (EventWrapper) finalConstructor.newInstance(event);
						pluginManager.callEvent(eventWrapper);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						System.err.println("Error calling event: " + e.getMessage());
					}
				});
			}
		}
	}

	private static void initEffectCommands() {
		MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent.class, event -> {
			if (!SkriptConfig.enableEffectCommands.value()) return;
			LuckPermsPlayer player = (LuckPermsPlayer) event.getPlayer();
			if (!player.hasPermission("skript.effectcommands")) return;
			String message = event.getRawMessage();
			if (!message.startsWith(SkriptConfig.effectCommandToken.value())) return;
			event.setCancelled(true);
			message = message.substring(1);

			EffectCommandEvent effectCommandEvent = new EffectCommandEvent(player);
			Bukkit.getPluginManager().callEvent(effectCommandEvent);
			if (effectCommandEvent.isCancelled()) return;
			try (RedirectingLogHandler log = new RedirectingLogHandler(player, null)) {
				SkriptLogger.startLogHandler(log);
				ParserInstance parserInstance = ParserInstance.get();
				parserInstance.setCurrentEvent("effect command", EffectCommandEvent.class);
				Effect effect = Effect.parse(message, null);
				TagResolver effectCommand = Placeholder.unparsed("effect_command", message);
				if (effect != null) {
					player.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <base_grey>Executing effect command: <yellow><effect_command>", effectCommand));
					if (SkriptConfig.logEffectCommands.value()) {
						MinecraftServer.getCommandManager().getConsoleSender().sendMessage(player.getUsername() + " issued effect command: " + message);
					}
					TriggerItem.walk(effect, effectCommandEvent);
					Variables.removeLocals(effectCommandEvent);
				} else player.sendMessage(SKRIPT_MINI_MESSAGE.deserialize("<skript_minestom_tag> <error_color>Couldn't find an effect under '<yellow><effect_command><error_color>'.", effectCommand));
			}
		});
	}

	private static void scheduleShutdownTasks() {
		MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
			String kickMsg = properties.getProperty(SERVER_STOPPING_MINIMESSAGE);
			Component kickComponent = BASIC_MINI_MESSAGE.deserialize(kickMsg);
			for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
				p.kick(kickComponent);
			}
			spark.shutdown();
			Plugin skript = Bukkit.getPluginManager().getPlugin("Skript");
			if (skript != null) skript.onDisable();
			LuckPermsMinestom.disable();
			MinestomTerminal.stop();
			System.exit(0); // sometimes server hangs so manually stop
		});
	}

}
