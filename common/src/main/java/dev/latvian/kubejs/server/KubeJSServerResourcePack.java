package dev.latvian.kubejs.server;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class KubeJSServerResourcePack extends KubeJSResourcePack {
	public KubeJSServerResourcePack() {
		super(PackType.SERVER_DATA);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		DataJsonGenerator generator = new DataJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateDataJsons(generator));
	}
}
