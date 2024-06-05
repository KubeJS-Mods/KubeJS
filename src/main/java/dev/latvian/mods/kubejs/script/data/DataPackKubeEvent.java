package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.util.JsonIO;
import net.minecraft.resources.ResourceLocation;

public class DataPackKubeEvent implements KubeEvent {
	private final VirtualKubeJSDataPack virtualDataPack;

	public DataPackKubeEvent(VirtualKubeJSDataPack d) {
		this.virtualDataPack = d;
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
}