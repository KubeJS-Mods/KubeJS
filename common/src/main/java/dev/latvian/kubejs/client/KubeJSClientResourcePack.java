package dev.latvian.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.HashMap;
import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {
	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		AssetJsonGenerator generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		Map<String, String> langMap = new HashMap<>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(langMap));

		JsonObject lang = new JsonObject();

		for (Map.Entry<String, String> entry : langMap.entrySet()) {
			lang.addProperty(entry.getKey(), entry.getValue());
		}

		generator.json(new ResourceLocation("kubejs_generated:lang/en_us"), lang);
	}
}
