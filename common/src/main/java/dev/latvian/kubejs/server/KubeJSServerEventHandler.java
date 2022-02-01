package dev.latvian.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.command.KubeJSCommands;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.SimpleWorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.RhinoException;
import me.shedaniel.architectury.event.events.CommandPerformEvent;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import me.shedaniel.architectury.event.events.TickEvent;
import me.shedaniel.architectury.hooks.LevelResourceHooks;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSServerEventHandler {
	private static final LevelResource PERSISTENT_DATA = LevelResourceHooks.create("kubejs_persistent_data.nbt");

	public static void init() {
		LifecycleEvent.SERVER_BEFORE_START.register(KubeJSServerEventHandler::serverAboutToStart);
		CommandRegistrationEvent.EVENT.register(KubeJSServerEventHandler::registerCommands);
		LifecycleEvent.SERVER_STARTED.register(KubeJSServerEventHandler::serverStarted);
		LifecycleEvent.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
		LifecycleEvent.SERVER_WORLD_SAVE.register(KubeJSServerEventHandler::serverWorldSave);
		TickEvent.SERVER_POST.register(KubeJSServerEventHandler::serverTick);
		CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
	}

	public static void serverAboutToStart(MinecraftServer server) {
		if (ServerJS.instance != null) {
			destroyServer();
		}

		ServerJS.instance = new ServerJS(server, ServerScriptManager.instance);

		Path p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				CompoundTag tag = NbtIo.readCompressed(p.toFile());

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
		//		new CommandRegistryEventJS(dispatcher, selection).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
	}

	public static void serverStarted(MinecraftServer server) {
		ServerJS.instance.overworld = new ServerWorldJS(ServerJS.instance, server.getLevel(Level.OVERWORLD));
		ServerJS.instance.levelMap.put("minecraft:overworld", ServerJS.instance.overworld);
		ServerJS.instance.worlds.add(ServerJS.instance.overworld);

		for (ServerLevel world : server.getAllLevels()) {
			if (world != ServerJS.instance.overworld.minecraftLevel) {
				ServerWorldJS w = new ServerWorldJS(ServerJS.instance, world);
				ServerJS.instance.levelMap.put(world.dimension().location().toString(), w);
			}
		}

		ServerJS.instance.updateWorldList();

		new AttachServerDataEvent(ServerJS.instance).invoke();
		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_LOAD);

		for (ServerWorldJS world : ServerJS.instance.worlds) {
			new AttachWorldDataEvent(world).invoke();
			new SimpleWorldEventJS(world).post(KubeJSEvents.WORLD_LOAD);
		}
	}

	public static void serverStopping(MinecraftServer server) {
		destroyServer();
	}

	public static void destroyServer() {
		ServerJS s = ServerJS.instance;

		for (PlayerDataJS<?, ?> p : s.playerMap.values()) {
			new SimplePlayerEventJS(p.getMinecraftPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
		}

		for (WorldJS w : s.levelMap.values()) {
			new SimpleWorldEventJS(w).post(KubeJSEvents.WORLD_UNLOAD);
		}

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_UNLOAD);
		s.release();
		ServerJS.instance = null;
	}

	private static void serverWorldSave(ServerLevel level) {
		ServerJS s = ServerJS.instance;
		Path p = level.getServer().getWorldPath(PERSISTENT_DATA);

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
		ServerJS s = ServerJS.instance;

		if (!s.scheduledEvents.isEmpty()) {
			long now = System.currentTimeMillis();
			Iterator<ScheduledEvent> eventIterator = s.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext()) {
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime()) {
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list) {
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
			long now = s.getOverworld().getTime();
			Iterator<ScheduledEvent> eventIterator = s.scheduledTickEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext()) {
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime()) {
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list) {
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

	public static InteractionResult command(CommandPerformEvent event) {
		if (new CommandEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_RUN)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}
}