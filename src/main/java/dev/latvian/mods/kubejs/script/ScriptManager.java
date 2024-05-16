package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.helpers.MiscHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScriptManager {
	private static final ThreadLocal<KubeJSContextFactory> CURRENT_CONTEXT = new ThreadLocal<>();

	@Nullable
	public static KubeJSContextFactory getCurrentContextFactory() {
		return CURRENT_CONTEXT.get();
	}

	public static KubeJSContext getCurrentContext() {
		return (KubeJSContext) Objects.requireNonNull(getCurrentContextFactory()).enter();
	}

	public final ScriptType scriptType;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	public KubeJSContextFactory contextFactory;
	public boolean canListenEvents;
	protected HolderLookup.Provider registries;

	public ScriptManager(ScriptType t) {
		scriptType = t;
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(scriptType);
	}

	public void unload() {
		packs.clear();
		scriptType.unload();
	}

	public void reload(@Nullable ResourceManager resourceManager) {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);

		unload();
		scriptType.console.writeToFile(LogType.INIT, "KubeJS " + KubeJS.thisMod.getVersion() + "; MC " + KubeJS.MC_VERSION_NUMBER + " NeoForge");
		scriptType.console.writeToFile(LogType.INIT, "Loaded plugins:");

		for (var plugin : KubeJSPlugins.getAll()) {
			scriptType.console.writeToFile(LogType.INIT, "- " + plugin.getClass().getName());
		}

		loadFromDirectory();

		if (resourceManager != null) {
			loadFromResources(resourceManager);
		}

		load();
	}

	private void loadFile(ScriptPack pack, ScriptFileInfo fileInfo, ScriptSource source) {
		try {
			fileInfo.preload(source);
			var skip = fileInfo.skipLoading();

			if (skip.isEmpty()) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, source));
			} else {
				scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
			}
		} catch (Throwable error) {
			scriptType.console.error("Failed to pre-load script file '" + fileInfo.location + "'", error);
		}
	}

	private void loadFromResources(ResourceManager resourceManager) {
		Map<String, List<ResourceLocation>> packMap = new HashMap<>();

		for (var resource : resourceManager.listResources("kubejs", s -> s.getPath().endsWith(".js") || s.getPath().endsWith(".ts") && !s.getPath().endsWith(".d.ts")).keySet()) {
			packMap.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (var entry : packMap.entrySet()) {
			var pack = new ScriptPack(this, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (var id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (var fileInfo : pack.info.scripts) {
				var scriptSource = (ScriptSource.FromResource) info -> resourceManager.getResourceOrThrow(info.id);
				loadFile(pack, fileInfo, scriptSource);
			}

			pack.scripts.sort(null);
			packs.put(pack.info.namespace, pack);
		}
	}

	public void loadFromDirectory() {
		if (Files.notExists(scriptType.path)) {
			try {
				Files.createDirectories(scriptType.path);
			} catch (Exception ex) {
				scriptType.console.error("Failed to create script directory", ex);
			}

			try (var out = Files.newOutputStream(scriptType.path.resolve("example.js"))) {
				out.write(("""
					// priority: 0

					// Visit the wiki for more info - https://kubejs.com/

					console.info('Hello, World! (Loaded\s""" + scriptType.name + " scripts)')\n\n").getBytes(StandardCharsets.UTF_8));
			} catch (Exception ex) {
				scriptType.console.error("Failed to write example.js", ex);
			}
		}

		var pack = new ScriptPack(this, new ScriptPackInfo(scriptType.path.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, scriptType.path, "");

		for (var fileInfo : pack.info.scripts) {
			var scriptSource = (ScriptSource.FromPath) info -> scriptType.path.resolve(info.file);
			loadFile(pack, fileInfo, scriptSource);
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	public void load() {
		var startAll = System.currentTimeMillis();

		contextFactory = new KubeJSContextFactory(this);
		CURRENT_CONTEXT.set(contextFactory);

		if (MiscHelper.get().isDataGen()) {
			firstLoad = false;
			scriptType.console.info("Skipping KubeJS script loading (DataGen)");
			return;
		}

		canListenEvents = true;

		var typeWrappers = new WrapperRegistry(scriptType, contextFactory.getTypeWrappers());
		// typeWrappers.removeAll();
		var customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(scriptType, contextFactory);

		for (var plugin : KubeJSPlugins.getAll()) {
			plugin.registerTypeWrappers(typeWrappers);
			plugin.registerCustomJavaToJsWrappers(customJavaToJsWrappersEvent);
		}

		for (var reg : BuiltInRegistries.REGISTRY.registryKeySet()) {
			var info = RegistryInfo.of(reg);

			if (info.autoWrap && info.objectBaseClass != Object.class && info.objectBaseClass != null) {
				try {
					typeWrappers.register(info.objectBaseClass, UtilsJS.cast(info));
				} catch (IllegalArgumentException ignored) {
					scriptType.console.info("Skipped registry type wrapper for " + info.key.location());
				}
			}
		}

		var i = 0;
		var t = 0;

		for (var pack : packs.values()) {
			try {
				for (var file : pack.scripts) {
					t++;
					var start = System.currentTimeMillis();

					try {
						file.load();
						i++;
						scriptType.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} catch (Throwable ex) {
						scriptType.console.error("", ex);
					}
				}
			} catch (Throwable ex) {
				scriptType.console.error("Failed to read script pack " + pack.info.namespace, ex);
			}
		}

		scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + scriptType.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s with " + scriptType.console.errors.size() + " errors and " + scriptType.console.warnings.size() + " warnings");
		firstLoad = false;
		canListenEvents = false;
	}
}