package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
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
		LifecycleEvent.SERVER_STARTED.register(KubeJSServerEventHandler::serverStarted);
		LifecycleEvent.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
		LifecycleEvent.SERVER_LEVEL_SAVE.register(KubeJSServerEventHandler::serverWorldSave);
		TickEvent.SERVER_POST.register(KubeJSServerEventHandler::serverTick);
		CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
	}

	public static void serverAboutToStart(MinecraftServer server) {
		if (ServerJS.instance != null) {
			destroyServer();
		}

		ServerJS.instance = new ServerJS(server, ServerScriptManager.instance);

		var p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				var tag = NbtIo.readCompressed(p.toFile());

				if (tag != null) {
					ServerJS.instance.persistentData.merge(tag);
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

	public static void serverStarted(MinecraftServer server) {
		ServerJS.instance.overworld = new ServerLevelJS(ServerJS.instance, server.getLevel(Level.OVERWORLD));
		ServerJS.instance.levelMap.put(Level.OVERWORLD.location(), ServerJS.instance.overworld);
		ServerJS.instance.allLevels.add(ServerJS.instance.overworld);

		for (var level : server.getAllLevels()) {
			if (level != ServerJS.instance.overworld.minecraftLevel) {
				var l = new ServerLevelJS(ServerJS.instance, level);
				ServerJS.instance.levelMap.put(level.dimension().location(), l);
			}
		}

		ServerJS.instance.updateWorldList();

		AttachDataEvent.forServer(ServerJS.instance).invoke();
		ServerEvents.LOADED.post(new ServerEventJS());

		for (var level : ServerJS.instance.allLevels) {
			AttachDataEvent.forLevel(level).invoke();
			LevelEvents.LOADED.post(level.getDimension(), new SimpleLevelEventJS(level));
		}
	}

	public static void serverStopping(MinecraftServer server) {
		destroyServer();
	}

	public static void destroyServer() {
		var s = ServerJS.instance;

		for (PlayerDataJS<?, ?> p : s.playerMap.values()) {
			PlayerEvents.LOGGED_OUT.post(new SimplePlayerEventJS(p.getMinecraftPlayer()));
		}

		for (var level : s.levelMap.values()) {
			LevelEvents.UNLOADED.post(level.getDimension(), new SimpleLevelEventJS(level));
		}

		ServerEvents.UNLOADED.post(new ServerEventJS());
		s.release();
		ServerJS.instance = null;
	}

	private static void serverWorldSave(ServerLevel level) {
		var s = ServerJS.instance;
		var p = level.getServer().getWorldPath(PERSISTENT_DATA);

		if (s != null && level.dimension() == Level.OVERWORLD) {
			Util.ioPool().execute(() -> {
				try {
					NbtIo.writeCompressed(s.persistentData, p.toFile());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public static void serverTick(MinecraftServer server) {
		var s = ServerJS.instance;

		if (!s.scheduledEvents.isEmpty()) {
			var now = System.currentTimeMillis();
			var eventIterator = s.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

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

		if (!s.scheduledTickEvents.isEmpty()) {
			var now = s.getOverworld().getTime();
			var eventIterator = s.scheduledTickEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

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

		ServerEvents.TICK.post(new ServerEventJS());
	}

	public static EventResult command(CommandPerformEvent event) {
		var e = new CommandEventJS(event);

		if (ServerEvents.COMMAND.post(e.getCommandName(), e)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}
}