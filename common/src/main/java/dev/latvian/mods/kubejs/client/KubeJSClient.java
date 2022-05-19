package dev.latvian.mods.kubejs.client;

import dev.architectury.hooks.PackRepositoryHooks;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCommon;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.level.ClientLevelJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.net.NetworkEventJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon {
	@Override
	public void init() {
		// You'd think that this is impossible, but not when you use runData gradle task
		if (Minecraft.getInstance() == null) {
			return;
		}

		reloadClientScripts();

		new KubeJSClientEventHandler().init();
		var list = Minecraft.getInstance().getResourcePackRepository();
		PackRepositoryHooks.addSource(list, new KubeJSResourcePackFinder());
		setup();

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clientInit);
	}

	@Override
	public void reloadClientInternal() {
		reloadClientScripts();
	}

	public static void reloadClientScripts() {
		KubeJSClientEventHandler.staticItemTooltips = null;
		KubeJS.clientScriptManager.unload();
		KubeJS.clientScriptManager.loadFromDirectory();
		KubeJS.clientScriptManager.load();
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
	public void clientBindings(BindingsEvent event) {
		event.add("Client", new ClientWrapper());
		event.add("Painter", Painter.INSTANCE);
	}

	private void setup() {
		new ClientEventJS().post(KubeJSEvents.CLIENT_INIT);
	}

	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data) {
		new NetworkEventJS(Minecraft.getInstance().player, channel, data).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
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

	@Override
	public LevelJS getClientLevel() {
		return ClientLevelJS.getInstance();
	}

	private void reload(PreparableReloadListener listener) {
		var start = System.currentTimeMillis();
		var mc = Minecraft.getInstance();
		listener.reload(CompletableFuture::completedFuture, mc.getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), mc).thenAccept(unused -> {
			/*
			long ms = System.currentTimeMillis() - start;

			if (ms < 1000L) {
				mc.player.sendMessage(new TextComponent("Reloaded in " + ms + "ms! You still may have to reload all assets with F3 + T"), Util.NIL_UUID);
			} else {
				mc.player.sendMessage(new TextComponent("Reloaded in " + Mth.ceil(ms / 1000D) + "s! You still may have to reload all assets with F3 + T"), Util.NIL_UUID);
			}
			 */

			mc.player.sendMessage(new TextComponent("Done! You still may have to reload all assets with F3 + T"), Util.NIL_UUID);
		});
	}

	@Override
	public void reloadTextures() {
		reload(Minecraft.getInstance().getTextureManager());
	}

	@Override
	public void reloadLang() {
		reload(Minecraft.getInstance().getLanguageManager());
	}

	@Override
	public boolean isClientButNotSelf(Player player) {
		return player instanceof AbstractClientPlayer && !(player instanceof LocalPlayer);
	}
}