package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GeneratedClientResourcePack extends GeneratedResourcePack {
	public static List<PackResources> inject(List<PackResources> packs) {
		// only add the resource pack if KubeJS has loaded
		// to prevent crashes on mod loading errors
		if (KubeJS.instance != null) {
			packs = new ArrayList<>(packs);

			int i = packs.size();

			for (int j = 1; j < packs.size(); j++) {
				if (packs.get(j) instanceof FilePackResources) {
					i = j;
					break;
				}
			}

			var injected = new ArrayList<PackResources>(2);
			injected.add(new GeneratedClientResourcePack());
			// injected.add(KubeJSFolderPackResources.PACK);

			for (var file : Objects.requireNonNull(KubeJSPaths.ASSETS.toFile().listFiles())) {
				if (file.isFile() && file.getName().endsWith(".zip")) {
					injected.add(new FilePackResources(file.getName(), file, false));
				}
			}

			packs.addAll(i, injected);
		}

		return packs;
	}

	public GeneratedClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generate(Map<ResourceLocation, GeneratedData> map) {
		var generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		var langMap = new HashMap<String, String>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(langMap));

		ClientEvents.HIGH_ASSETS.post(ScriptType.CLIENT, new GenerateClientAssetsEventJS(generator, langMap));

		var lang = new JsonObject();

		for (var entry : langMap.entrySet()) {
			lang.addProperty(entry.getKey(), entry.getValue());
		}

		generator.json(new ResourceLocation("kubejs_generated:lang/en_us"), lang);
	}
}
