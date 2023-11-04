package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.nio.file.Files;
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
					injected.add(new FilePackResources(file.getName(), file, false));
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

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateAssetJsons(generator);
		}

		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		var langMap = new HashMap<LangEventJS.Key, String>();
		var langEvents = new HashMap<String, LangEventJS>();
		var enUsLangEvent = langEvents.computeIfAbsent("en_us", s -> new LangEventJS(s, langMap));

		if (Platform.isModLoaded("jade")) {
			for (var mod : PlatformWrapper.getMods().values()) {
				if (!mod.getCustomName().isEmpty()) {
					enUsLangEvent.add(KubeJS.MOD_ID, "jade.modName." + mod.getId(), mod.getCustomName());
				}
			}
		}

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateLang(enUsLangEvent);
		}

		KubeJSPlugins.forEachPlugin(p -> p.generateLang(enUsLangEvent));

		ClientEvents.HIGH_ASSETS.post(ScriptType.CLIENT, new GenerateClientAssetsEventJS(generator));

		for (var lang : ClientEvents.LANG.findUniqueExtraIds(ScriptType.CLIENT)) {
			var l = String.valueOf(lang);

			if (LangEventJS.PATTERN.matcher(l).matches()) {
				ClientEvents.LANG.post(ScriptType.CLIENT, l, langEvents.computeIfAbsent(l, k -> new LangEventJS(k, langMap)));
			} else {
				ConsoleJS.CLIENT.error("Invalid language key: " + l);
			}
		}

		try {
			for (var dir : Files.list(KubeJSPaths.ASSETS).filter(Files::isDirectory).toList()) {
				var ns = dir.getFileName().toString();

				var langDir = dir.resolve("lang");

				if (Files.exists(langDir) && Files.isDirectory(langDir)) {
					for (var path : Files.list(langDir).filter(Files::isRegularFile).filter(Files::isReadable).toList()) {
						var fileName = path.getFileName().toString();

						if (fileName.endsWith(".json")) {
							try (var reader = Files.newBufferedReader(path)) {
								var json = JsonUtils.GSON.fromJson(reader, JsonObject.class);
								var lang = fileName.substring(0, fileName.length() - 5);

								for (var entry : json.entrySet()) {
									langMap.put(new LangEventJS.Key(ns, lang, entry.getKey()), entry.getValue().getAsString());
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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

	@Override
	protected boolean forgetFile(String path) {
		return path.endsWith(".png") || path.endsWith(".ogg");
	}

	@Override
	protected boolean skipFile(GeneratedData data) {
		return data.id().getPath().startsWith("lang/");
	}
}
