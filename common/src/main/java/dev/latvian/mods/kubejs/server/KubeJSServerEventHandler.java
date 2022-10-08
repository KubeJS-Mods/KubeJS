package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
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
		LifecycleEvent.SERVER_STOPPED.register(KubeJSServerEventHandler::serverStopped);
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
		new CommandRegistryEventJS(dispatcher, selection).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
	}

	public static void serverStarted(MinecraftServer server) {
		ServerJS.instance.overworld = new ServerLevelJS(ServerJS.instance, server.getLevel(Level.OVERWORLD));
		ServerJS.instance.levelMap.put(DimensionType.OVERWORLD_LOCATION.location(), ServerJS.instance.overworld);
		ServerJS.instance.allLevels.add(ServerJS.instance.overworld);

		for (var level : server.getAllLevels()) {
			if (level != ServerJS.instance.overworld.minecraftLevel) {
				var l = new ServerLevelJS(ServerJS.instance, level);
				ServerJS.instance.levelMap.put(level.dimension().location(), l);
			}
		}

		ServerJS.instance.updateWorldList();

		AttachDataEvent.forServer(ServerJS.instance).invoke();
		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_LOAD);

		for (var level : ServerJS.instance.allLevels) {
			AttachDataEvent.forLevel(level).invoke();
			new SimpleLevelEventJS(level).post(KubeJSEvents.LEVEL_LOAD);
		}
	}

	public static void serverStopped(MinecraftServer server) {
		destroyServer();
	}

	public static void destroyServer() {
		var s = ServerJS.instance;

		for (PlayerDataJS<?, ?> p : s.playerMap.values()) {
			new SimplePlayerEventJS(p.getMinecraftPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
		}

		for (var w : s.levelMap.values()) {
			new SimpleLevelEventJS(w).post(KubeJSEvents.LEVEL_UNLOAD);
		}

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_UNLOAD);
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

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_TICK);
	}

	public static EventResult command(CommandPerformEvent event) {
		if (new CommandEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_RUN)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}
}