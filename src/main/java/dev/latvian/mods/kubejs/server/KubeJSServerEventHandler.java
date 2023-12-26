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
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;

public class KubeJSServerEventHandler {
	private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

	public static void init() {
		LifecycleEvent.SERVER_BEFORE_START.register(KubeJSServerEventHandler::serverBeforeStart);
		CommandRegistrationEvent.EVENT.register(KubeJSServerEventHandler::registerCommands);
		LifecycleEvent.SERVER_STARTING.register(KubeJSServerEventHandler::serverStarting);
		LifecycleEvent.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
		LifecycleEvent.SERVER_STOPPED.register(KubeJSServerEventHandler::serverStopped);
		LifecycleEvent.SERVER_LEVEL_SAVE.register(KubeJSServerEventHandler::serverLevelSaved);
		LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSServerEventHandler::serverLevelLoaded);
		CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
	}

	public static void serverBeforeStart(MinecraftServer server) {
		UtilsJS.staticServer = server;
		UtilsJS.staticRegistryAccess = server.registryAccess();

		var p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				var tag = NbtIo.readCompressed(p, NbtAccounter.unlimitedHeap());

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

	private static void serverStarting(MinecraftServer server) {
		ServerEvents.LOADED.post(ScriptType.SERVER, new ServerEventJS(server));
	}

	private static void serverStopping(MinecraftServer server) {
		ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerEventJS(server));
	}

	private static void serverStopped(MinecraftServer server) {
		UtilsJS.staticServer = null;
		UtilsJS.staticRegistryAccess = RegistryAccess.EMPTY;
	}

	private static void serverLevelLoaded(ServerLevel level) {
		if (LevelEvents.LOADED.hasListeners()) {
			LevelEvents.LOADED.post(new SimpleLevelEventJS(level), level.dimension().location());
		}
	}

	private static void serverLevelSaved(ServerLevel level) {
		var p = level.getServer().getWorldPath(PERSISTENT_DATA);

		if (level.dimension() == Level.OVERWORLD) {
			Util.ioPool().execute(() -> {
				try {
					NbtIo.writeCompressed(level.getServer().kjs$getPersistentData(), p);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public static EventResult command(CommandPerformEvent event) {
		if (ServerEvents.COMMAND.hasListeners()) {
			var e = new CommandEventJS(event);
			return ServerEvents.COMMAND.post(e, e.getCommandName()).arch();
		}

		return EventResult.pass();
	}
}