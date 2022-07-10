package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

/**
 * @author LatvianModder
 */
public class DataPackEventJS extends ServerEventJS {
	public static final EventHandler LOW_EVENT = EventHandler.server(DataPackEventJS.class).legacy("server.datapack.low_priority");
	public static final EventHandler HIGH_EVENT = EventHandler.server(DataPackEventJS.class).legacy("server.datapack.high_priority");

	private final VirtualKubeJSDataPack virtualDataPack;
	private final MultiPackResourceManager wrappedManager;

	public DataPackEventJS(VirtualKubeJSDataPack d, MultiPackResourceManager rm) {
		this.virtualDataPack = d;
		this.wrappedManager = rm;
	}

	public void add(ResourceLocation id, String content) {
		virtualDataPack.addData(id, content);
	}

	public void addJson(ResourceLocation id, JsonElement json) {
		if (json != null) {
			// append .json to the filename if it doesn't have it already
			id = id.getPath().endsWith(".json") ? id : new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
			add(id, JsonIO.toString(json));
		}
	}

	@Override
	protected void afterPosted(boolean result) {
		for (var namespace : virtualDataPack.getNamespaces(PackType.SERVER_DATA)) {
			// this is terrible, but it works for now
			wrappedManager.namespacedManagers.computeIfAbsent(namespace, ns -> new FallbackResourceManager(PackType.SERVER_DATA, ns)).push(virtualDataPack);
		}
	}
}