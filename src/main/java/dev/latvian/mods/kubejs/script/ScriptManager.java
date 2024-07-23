package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptManager {
	public final ScriptType scriptType;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public KubeJSContextFactory contextFactory;
	public boolean canListenEvents;

	public ScriptManager(ScriptType t) {
		scriptType = t;
		packs = new LinkedHashMap<>();
		classFilter = KubeJSPlugins.createClassFilter(scriptType);
	}

	public RegistryAccessContainer getRegistries() {
		return RegistryAccessContainer.current;
	}

	public void unload() {
		packs.clear();
		scriptType.unload();
	}

	public void reload() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);

		unload();
		scriptType.console.writeToFile(LogType.INIT, "KubeJS " + KubeJS.VERSION + "; MC " + KubeJS.MC_VERSION_NUMBER + " NeoForge");
		scriptType.console.writeToFile(LogType.INIT, "Loaded plugins:");

		for (var plugin : KubeJSPlugins.getAll()) {
			scriptType.console.writeToFile(LogType.INIT, "- " + plugin.getClass().getName());
		}

		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::beforeScriptsLoaded);
		loadFromDirectory();
		load();
		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::afterScriptsLoaded);
	}

	public void loadPackFromDirectory(Path path, String name, boolean exampleFile) {
		if (Files.notExists(path)) {
			if (!exampleFile) {
				return;
			}

			try {
				Files.createDirectories(path);
			} catch (Exception ex) {
				scriptType.console.error("Failed to create script directory", ex);
			}

			try (var out = Files.newOutputStream(path.resolve("main.js"))) {
				out.write(("""
					// Visit the wiki for more info - https://kubejs.com/
					console.info('Hello, World! (Loaded\s""" + name + " example script)')\n\n").getBytes(StandardCharsets.UTF_8));
			} catch (Exception ex) {
				scriptType.console.error("Failed to write main.js", ex);
			}
		}

		var pack = new ScriptPack(this, new ScriptPackInfo(path.getFileName().toString(), ""));

		if (Files.exists(path)) {
			KubeJS.loadScripts(pack, path, "");

			for (var fileInfo : pack.info.scripts) {
				loadFile(pack, fileInfo);
			}

			pack.scripts.sort(null);
		}

		packs.put(pack.info.namespace, pack);
	}

	private void loadFile(ScriptPack pack, ScriptFileInfo fileInfo) {
		try {
			var file = new ScriptFile(pack, fileInfo);
			var skip = file.skipLoading();

			if (skip.isEmpty()) {
				pack.scripts.add(file);
			} else {
				scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
			}
		} catch (Throwable error) {
			scriptType.console.error("Failed to pre-load script file '" + fileInfo.location + "'", error);
		}
	}

	public void loadFromDirectory() {
		loadPackFromDirectory(scriptType.path, scriptType.name, true);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	private void load() {
		var startAll = System.currentTimeMillis();

		contextFactory = new KubeJSContextFactory(this);
		scriptType.console.contextFactory = new WeakReference<>(contextFactory);

		if (PlatformWrapper.isGeneratingData()) {
			scriptType.console.info("Skipping KubeJS script loading (DataGen)");
			return;
		}

		canListenEvents = true;

		var typeWrappers = new TypeWrapperRegistry(scriptType, contextFactory.getTypeWrappers());
		// typeWrappers.removeAll();

		for (var plugin : KubeJSPlugins.getAll()) {
			plugin.registerTypeWrappers(typeWrappers);
		}

		var i = 0;
		var t = 0;

		var cx = (KubeJSContext) contextFactory.enter();

		var watchingFiles = new ArrayList<ScriptFile>();

		for (var pack : packs.values()) {
			try {
				for (var file : pack.scripts) {
					t++;
					var start = System.currentTimeMillis();

					try {
						file.load(cx);
						i++;
						scriptType.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
						watchingFiles.add(file);
					} catch (Throwable ex) {
						scriptType.console.error("", ex);
					}
				}
			} catch (Throwable ex) {
				scriptType.console.error("Failed to read script pack " + pack.info.namespace, ex);
			}
		}

		loadAdditional();
		scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + scriptType.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s with " + scriptType.console.errors.size() + " errors and " + scriptType.console.warnings.size() + " warnings");
		canListenEvents = false;

		if (!watchingFiles.isEmpty() && DevProperties.get().reloadOnFileSave) {
			scriptType.fileWatcherThread = new KubeJSFileWatcherThread(scriptType, watchingFiles.toArray(new ScriptFile[0]), this::fullReload);
			scriptType.fileWatcherThread.start();
		}
	}

	public void loadAdditional() {
	}

	protected void fullReload() {
		KubeJS.PROXY.runInMainThread(this::reload);
	}
}