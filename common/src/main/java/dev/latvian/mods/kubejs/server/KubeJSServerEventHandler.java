package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSServerEventHandler {
	private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

	public static void init() {
		LifecycleEvent.SERVER_BEFORE_START.register(KubeJSServerEventHandler::serverAboutToStart);
		CommandRegistrationEvent.EVENT.register(KubeJSServerEventHandler::registerCommands);
		LifecycleEvent.SERVER_BEFORE_START.register(KubeJSServerEventHandler::serverBeforeStarting);
		LifecycleEvent.SERVER_STARTING.register(KubeJSServerEventHandler::serverStarting);
		LifecycleEvent.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
		LifecycleEvent.SERVER_STOPPED.register(KubeJSServerEventHandler::serverStopped);
		LifecycleEvent.SERVER_LEVEL_SAVE.register(KubeJSServerEventHandler::serverLevelSaved);
		LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSServerEventHandler::serverLevelLoaded);
		CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
	}

	public static void serverAboutToStart(MinecraftServer server) {
		var p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				var tag = NbtIo.readCompressed(p.toFile());

				if (tag != null) {
					server.kjs$getPersistentData().merge(tag);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		KubeJSCommands.register(dispatcher);
		ServerEvents.COMMAND_REGISTRY.post(new CommandRegistryEventJS(dispatcher, selection));
	}

	private static void serverBeforeStarting(MinecraftServer server) {
		UtilsJS.staticServer = server;
	}

	private static void serverStarting(MinecraftServer server) {
		AttachDataEvent.forServer(server).invoke();
		ServerEvents.LOADED.post(new ServerEventJS(server));
	}

	private static void serverStopping(MinecraftServer server) {
		for (PlayerDataJS<?, ?> p : server.kjs$getPlayerMap().values()) {
			PlayerEvents.LOGGED_OUT.post(new SimplePlayerEventJS(p.getMinecraftPlayer()));
		}

		for (var level : server.kjs$getLevelMap().values()) {
			LevelEvents.UNLOADED.post(level.getDimension(), new SimpleLevelEventJS(level));
		}

		ServerEvents.UNLOADED.post(new ServerEventJS(server));
	}

	private static void serverStopped(MinecraftServer server) {
		UtilsJS.staticServer = null;
	}

	private static void serverLevelLoaded(ServerLevel level) {
		var l = level.getServer().kjs$wrapMinecraftLevel(level);
		AttachDataEvent.forLevel(l).invoke();
		LevelEvents.LOADED.post(level.dimension().location(), new SimpleLevelEventJS(l));
		level.getServer().kjs$updateWorldList();
	}

	private static void serverLevelSaved(ServerLevel level) {
		var p = level.getServer().getWorldPath(PERSISTENT_DATA);

		if (level.dimension() == Level.OVERWORLD) {
			Util.ioPool().execute(() -> {
				try {
					NbtIo.writeCompressed(level.getServer().kjs$getPersistentData(), p.toFile());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public static void tickScheduledEvents(long now, List<ScheduledEvent> kjs$scheduledEvents) {
		if (!kjs$scheduledEvents.isEmpty()) {
			var eventIterator = kjs$scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>(0);

			while (eventIterator.hasNext()) {
				var e = eventIterator.next();

				if (now >= e.getEndTime()) {
					list.add(e);
					eventIterator.remove();
				}
			}

			for (var e : list) {
				try {
					e.call();
				} catch (RhinoException ex) {
					ConsoleJS.SERVER.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static EventResult command(CommandPerformEvent event) {
		var e = new CommandEventJS(event);

		if (ServerEvents.COMMAND.post(e.getCommandName(), e)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}
}