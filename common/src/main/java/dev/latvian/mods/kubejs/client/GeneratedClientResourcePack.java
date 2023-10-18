package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.client.Minecraft;
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
	public static List<PackResources> inject(Minecraft client, List<PackResources> packs) {
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
			injected.add(new GeneratedClientResourcePack(client));

			for (var file : Objects.requireNonNull(KubeJSPaths.ASSETS.toFile().listFiles())) {
				if (file.isFile() && file.getName().endsWith(".zip")) {
					injected.add(new FilePackResources(file));
				}
			}

			packs.addAll(i, injected);
		}

		return packs;
	}

	public final Minecraft client;

	public GeneratedClientResourcePack(Minecraft client) {
		super(PackType.CLIENT_RESOURCES);
		this.client = client;
	}

	@Override
	public void generate(Map<ResourceLocation, GeneratedData> map) {
		var generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		var langMap = new HashMap<LangEventJS.Key, String>();
		var langEvents = new HashMap<String, LangEventJS>();
		var enUsLangEvent = langEvents.computeIfAbsent("en_us", s -> new LangEventJS(s, langMap));

		KubeJSPlugins.forEachPlugin(p -> p.generateLang(enUsLangEvent));

		var oldMap = new HashMap<String, String>();
		KubeJSPlugins.forEachPlugin(p -> p.generateLang(oldMap));
		oldMap.forEach(enUsLangEvent::add);

		ClientEvents.HIGH_ASSETS.post(ScriptType.CLIENT, new GenerateClientAssetsEventJS(generator));

		for (var lang : ClientEvents.LANG.findUniqueExtraIds(ScriptType.CLIENT)) {
			var l = String.valueOf(lang);

			if (LangEventJS.PATTERN.matcher(l).matches()) {
				ClientEvents.LANG.post(ScriptType.CLIENT, l, langEvents.computeIfAbsent(l, k -> new LangEventJS(l, langMap)));
			} else {
				ConsoleJS.CLIENT.error("Invalid language key: " + l);
			}
		}

		var finalMap = new HashMap<String, Map<String, JsonObject>>();

		for (var entry : langMap.entrySet()) {
			var ns = finalMap.computeIfAbsent(entry.getKey().namespace(), s -> new HashMap<>());
			var lang = ns.computeIfAbsent(entry.getKey().lang(), s -> new JsonObject());
			lang.addProperty(entry.getKey().key(), entry.getValue());
		}

		for (var e1 : finalMap.entrySet()) { // namespaces
			for (var e2 : e1.getValue().entrySet()) { // languages
				generator.json(new ResourceLocation(e1.getKey() + ":lang/" + e2.getKey()), e2.getValue());
			}
		}
	}
}
