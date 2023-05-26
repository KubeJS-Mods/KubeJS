package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

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

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
		KubeJSCommands.register(dispatcher);

		if (ServerEvents.COMMAND_REGISTRY.hasListeners()) {
			ServerEvents.COMMAND_REGISTRY.post(ScriptType.SERVER, new CommandRegistryEventJS(dispatcher, registry, selection));
		}
	}

	private static void serverBeforeStarting(MinecraftServer server) {
		UtilsJS.staticServer = server;
	}

	private static void serverStarting(MinecraftServer server) {
		ServerEvents.LOADED.post(ScriptType.SERVER, new ServerEventJS(server));
	}

	private static void serverStopping(MinecraftServer server) {
		ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerEventJS(server));
	}

	private static void serverStopped(MinecraftServer server) {
		UtilsJS.staticServer = null;
	}

	private static void serverLevelLoaded(ServerLevel level) {
		if (LevelEvents.LOADED.hasListeners()) {
			LevelEvents.LOADED.post(ScriptType.SERVER, level.dimension().location(), new SimpleLevelEventJS(level));
		}
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

	public static void tickScheduledEvents(long nowMs, long nowTicks, List<ScheduledEvent> kjs$scheduledEvents) {
		if (!kjs$scheduledEvents.isEmpty()) {
			var eventIterator = kjs$scheduledEvents.iterator();
			var list = new LinkedList<ScheduledEvent>();

			while (eventIterator.hasNext()) {
				var e = eventIterator.next();

				if (e.check(nowMs, nowTicks)) {
					list.add(e);
					eventIterator.remove();
				}
			}

			for (var e : list) {
				try {
					e.callback.onCallback(e);
				} catch (RhinoException ex) {
					ConsoleJS.SERVER.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static EventResult command(CommandPerformEvent event) {
		if (ServerEvents.COMMAND.hasListeners()) {
			var e = new CommandEventJS(event);
			return ServerEvents.COMMAND.post(ScriptType.SERVER, e.getCommandName(), e).arch();
		}

		return EventResult.pass();
	}
}