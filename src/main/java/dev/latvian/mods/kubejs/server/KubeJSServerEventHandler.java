package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSServerEventHandler {
	private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		KubeJSCommands.register(event.getDispatcher());

		if (ServerEvents.COMMAND_REGISTRY.hasListeners()) {
			ServerEvents.COMMAND_REGISTRY.post(ScriptType.SERVER, new CommandRegistryKubeEvent(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
		}
	}

	@SubscribeEvent
	public static void serverBeforeStart(ServerAboutToStartEvent event) {
		var server = event.getServer();

		var p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				var tag = NbtIo.readCompressed(p, NbtAccounter.unlimitedHeap());

				if (tag != null) {
					var t = tag.getCompound("__restore_inventories");

					if (!t.isEmpty()) {
						tag.remove("__restore_inventories");

						var playerMap = server.kjs$restoreInventories();

						for (var key : t.getAllKeys()) {
							var list = t.getList(key, 10);
							var map = playerMap.computeIfAbsent(UUID.fromString(key), k -> new HashMap<>());

							for (var tag2 : list) {
								var slot = ((CompoundTag) tag2).getShort("Slot");
								var stack = ItemStack.parse(server.registryAccess(), tag2);

								if (stack.isPresent()) {
									map.put((int) slot, stack.get());
								}
							}
						}
					}

					server.kjs$getPersistentData().merge(tag);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void serverStarting(ServerStartingEvent event) {
		ServerEvents.LOADED.post(ScriptType.SERVER, new ServerKubeEvent(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopping(ServerStoppingEvent event) {
		ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerKubeEvent(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopped(ServerStoppedEvent event) {
		UtilsJS.staticRegistries = RegistryAccess.EMPTY;
	}

	@SubscribeEvent
	public static void serverLevelLoaded(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
			LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	@SubscribeEvent
	public static void serverLevelSaved(LevelEvent.Save event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.SAVED.hasListeners(level.dimension())) {
			LevelEvents.SAVED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}

		if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
			var serverData = level.getServer().kjs$getPersistentData().copy();
			var p = level.getServer().getWorldPath(PERSISTENT_DATA);

			var playerMap = level.getServer().kjs$restoreInventories();

			if (!playerMap.isEmpty()) {
				var nbt = new CompoundTag();

				for (var entry : playerMap.entrySet()) {
					var list = new ListTag();

					for (var entry2 : entry.getValue().entrySet()) {
						var tag = new CompoundTag();
						tag.putShort("Slot", entry2.getKey().shortValue());
						entry2.getValue().save(level.registryAccess(), tag);
						list.add(tag);
					}

					nbt.put(entry.getKey().toString(), list);
				}

				serverData.put("__restore_inventories", nbt);
			}

			Util.ioPool().execute(() -> {
				try {
					NbtIo.writeCompressed(serverData, p);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	@SubscribeEvent
	public static void command(CommandEvent event) {
		if (ServerEvents.COMMAND.hasListeners()) {
			var e = new CommandKubeEvent(event);

			if (ServerEvents.COMMAND.hasListeners(e.getCommandName())) {
				ServerEvents.COMMAND.post(e, e.getCommandName()).applyCancel(event);
			}
		}
	}
}