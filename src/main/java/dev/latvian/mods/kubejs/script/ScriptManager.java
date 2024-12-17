package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
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

		long start = System.currentTimeMillis();

		KubeJSWeb.broadcastUpdate("before_scripts_loaded", "", () -> {
			var broadcast = new JsonObject();
			broadcast.addProperty("type", scriptType.name);
			broadcast.addProperty("time", start);
			return broadcast;
		});

		unload();
		scriptType.console.writeToFile(LogType.INIT, "KubeJS " + KubeJS.VERSION + "; MC " + KubeJS.MC_VERSION_NUMBER + " NeoForge");
		scriptType.console.writeToFile(LogType.INIT, "Loaded plugins:");

		for (var plugin : KubeJSPlugins.getAll()) {
			scriptType.console.writeToFile(LogType.INIT, "- " + plugin.getClass().getName());
		}

		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::beforeScriptsLoaded);
		loadFromDirectory();
		load(start);
		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::afterScriptsLoaded);
	}

	public void collectScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}

		final var pathPrefix = path;

		try {
			for (var file : Files.walk(dir, 10, FileVisitOption.FOLLOW_LINKS).filter(Files::isRegularFile).toList()) {
				var fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');

				if (fileName.endsWith(".js") || fileName.endsWith(".ts") && !fileName.endsWith(".d.ts")) {
					pack.info.scripts.add(new ScriptFileInfo(pack.info, file, pathPrefix + fileName));
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
			collectScripts(pack, path, "");

			for (var fileInfo : pack.info.scripts) {
				loadFile(pack, fileInfo);
			}
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

	private void load(long startAll) {
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
				pack.scripts.sort(null);

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
		long end = System.currentTimeMillis();
		long ms = end - startAll;

		scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + scriptType.name + " scripts in " + ms / 1000D + " s with " + scriptType.console.errors.size() + " errors and " + scriptType.console.warnings.size() + " warnings");
		canListenEvents = false;

		if (!watchingFiles.isEmpty() && DevProperties.get().reloadOnFileSave) {
			scriptType.fileWatcherThread = new KubeJSFileWatcherThread(scriptType, watchingFiles.toArray(new ScriptFile[0]), this::fullReload);
			scriptType.fileWatcherThread.start();
		}

		int t1 = t;
		int i1 = i;

		KubeJSWeb.broadcastUpdate("after_scripts_loaded", "", () -> {
			var broadcast = new JsonObject();
			broadcast.addProperty("type", scriptType.name);
			broadcast.addProperty("total", t1);
			broadcast.addProperty("successful", i1);
			broadcast.addProperty("errors", scriptType.console.errors.size());
			broadcast.addProperty("warnings", scriptType.console.warnings.size());
			broadcast.addProperty("time", end);
			broadcast.addProperty("duration", ms);
			return broadcast;
		});
	}

	public void loadAdditional() {
	}

	protected void fullReload() {
		KubeJS.PROXY.runInMainThread(this::reload);
	}
}