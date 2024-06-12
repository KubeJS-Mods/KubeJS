package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCommon;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.net.NetworkKubeEvent;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KubeJSClient extends KubeJSCommon {
	public static KeyMapping guiKey;
	public static KeyMapping inGameKey;

	@Override
	public void init() {
		var mc = Minecraft.getInstance();
		// You'd think that this is impossible, but not when you use runData gradle task
		if (mc == null) {
			return;
		}

		reloadClientScripts(mc);

		mc.getResourcePackRepository().addPackFinder(new KubeJSResourcePackFinder());

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clientInit);
	}

	@Override
	public void reloadClientInternal() {
		var mc = Minecraft.getInstance();
		if (mc != null) {
			reloadClientScripts(mc);
		}
	}

	public static void reloadClientScripts(Minecraft mc) {
		KubeJSClientEventHandler.staticItemTooltips = null;
		KubeJS.getClientScriptManager().reload();
	}

	public static void copyDefaultOptionsFile(File optionsFile) {
		if (!optionsFile.exists()) {
			var defOptions = KubeJSPaths.CONFIG.resolve("defaultoptions.txt");

			if (Files.exists(defOptions)) {
				try {
					KubeJS.LOGGER.info("Loaded default options from kubejs/config/defaultoptions.txt");
					Files.copy(defOptions, optionsFile.toPath());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handleDataFromServerPacket(String channel, @Nullable CompoundTag data) {
		if (NetworkEvents.DATA_RECEIVED.hasListeners(channel)) {
			NetworkEvents.DATA_RECEIVED.post(ScriptType.CLIENT, channel, new NetworkKubeEvent(Minecraft.getInstance().player, channel, data));
		}
	}

	@Override
	@Nullable
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void paint(CompoundTag tag) {
		Painter.INSTANCE.paint(tag);
	}

	private void reload(PreparableReloadListener listener) {
		var start = System.currentTimeMillis();
		var mc = Minecraft.getInstance();
		mc.getResourceManager().getResource(GeneratedData.INTERNAL_RELOAD.id());

		listener.reload(CompletableFuture::completedFuture, mc.getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), mc).thenAccept(unused -> {
			/*
			long ms = System.currentTimeMillis() - start;

			if (ms < 1000L) {
				mc.player.sendMessage(Component.literal("Reloaded in " + ms + "ms! You still may have to reload all assets with F3 + T"), Util.NIL_UUID);
			} else {
				mc.player.sendMessage(Component.literal("Reloaded in " + Mth.ceil(ms / 1000D) + "s! You still may have to reload all assets with F3 + T"), Util.NIL_UUID);
			}
			 */

			mc.player.sendSystemMessage(Component.literal("Done! You still may have to reload all assets with F3 + T"));
		});
	}

	@Override
	public void reloadTextures() {
		reload(Minecraft.getInstance().getTextureManager());
	}

	@Override
	public void reloadLang() {
		var mc = Minecraft.getInstance();
		if (mc != null) {
			reloadClientScripts(mc);
			reload(mc.getLanguageManager());
		}
	}

	@Override
	public void generateTypings(CommandSourceStack source) {
		source.sendSuccess(() -> Component.literal("WIP!"), false);
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		ClientProperties.reload();
	}

	@Override
	public void reloadStartupScripts(boolean dedicated) {
		var mc = Minecraft.getInstance();

		if (mc.player != null) {
			CreativeModeTabs.CACHED_PARAMETERS = null;
			CreativeModeTabs.tryRebuildTabContents(mc.player.connection.enabledFeatures(), mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get(), mc.level.registryAccess());
		}
	}

	@Override
	public void export(List<ExportablePackResources> packs) {
		for (var pack : Minecraft.getInstance().getResourceManager().listPacks().toList()) {
			if (pack instanceof ExportablePackResources e && !packs.contains(e)) {
				packs.add(e);
			}
		}
	}

	@Override
	public void openErrors(ScriptType type) {
		Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new KubeJSErrorScreen(Minecraft.getInstance().screen, type.console)));
	}

	@Override
	public void openErrors(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
		Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new KubeJSErrorScreen(Minecraft.getInstance().screen, type, null, errors, warnings)));
	}
}