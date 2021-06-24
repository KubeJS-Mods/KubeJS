package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.net.NetworkEventJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import me.shedaniel.architectury.hooks.PackRepositoryHooks;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon {
	public static final Map<String, Overlay> activeOverlays = new LinkedHashMap<>();

	@Override
	public void init() {
		if (Minecraft.getInstance() == null) // You'd think that this is impossible, but not when you use runData gradle task
		{
			return;
		}

		reloadClientScripts();

		new KubeJSClientEventHandler().init();
		PackRepository list = Minecraft.getInstance().getResourcePackRepository();
		PackRepositoryHooks.addSource(list, new KubeJSResourcePackFinder());
		setup();
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
			Path defOptions = KubeJSPaths.CONFIG.resolve("defaultoptions.txt");

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
		event.add("client", new ClientWrapper());
	}

	private void setup() {
		new EventJS().post(ScriptType.CLIENT, KubeJSEvents.CLIENT_INIT);
	}

	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data) {
		new NetworkEventJS(Minecraft.getInstance().player, channel, MapJS.of(data)).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
	}

	@Override
	@Nullable
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void openOverlay(Overlay o) {
		activeOverlays.put(o.id, o);
	}

	@Override
	public void closeOverlay(String id) {
		activeOverlays.remove(id);
	}

	@Override
	public WorldJS getClientWorld() {
		return ClientWorldJS.getInstance();
	}

	@Override
	public void reloadTextures() {
		Minecraft.getInstance().getTextureManager().reload(CompletableFuture::completedFuture, Minecraft.getInstance().getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), Minecraft.getInstance());
	}

	@Override
	public void reloadLang() {
		Minecraft.getInstance().getLanguageManager().reload(CompletableFuture::completedFuture, Minecraft.getInstance().getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), Minecraft.getInstance());
	}
}