package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {
	public static List<PackResources> inject(List<PackResources> packs) {
		List<PackResources> injected = new ArrayList<>(packs);
		// only add the resource pack if KubeJS has loaded
		// to prevent crashes on mod loading errors
		if (KubeJS.instance != null) {
			injected.add(new KubeJSClientResourcePack());
		}
		return injected;
	}

	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		var generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		Map<String, String> langMap = new HashMap<>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(langMap));

		new ClientGenerateAssetsEventJS(generator, langMap).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_GENERATE_ASSETS);

		var lang = new JsonObject();

		for (var entry : langMap.entrySet()) {
			lang.addProperty(entry.getKey(), entry.getValue());
		}

		generator.json(new ResourceLocation("kubejs_generated:lang/en_us"), lang);
	}
}
