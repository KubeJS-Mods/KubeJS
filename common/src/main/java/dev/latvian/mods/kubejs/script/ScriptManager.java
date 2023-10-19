package dev.latvian.mods.kubejs.script;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScriptManager implements ClassShutter {
	private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<>();

	@Nullable
	public static Context getCurrentContext() {
		return CURRENT_CONTEXT.get();
	}

	public final ScriptType scriptType;
	public final Path directory;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	public Context context;
	public Scriptable topLevelScope;
	private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;
	public boolean canListenEvents;

	public ScriptManager(ScriptType t, Path p) {
		scriptType = t;
		directory = p;
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(scriptType);
	}

	public void unload() {
		packs.clear();
		scriptType.unload();
		javaClassCache = null;
	}

	public void reload(@Nullable ResourceManager resourceManager) {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);

		unload();
		scriptType.console.writeToFile("INIT", "KubeJS " + KubeJS.thisMod.getVersion() + "; MC " + KubeJS.MC_VERSION_NUMBER + " " + PlatformWrapper.getName());
		scriptType.console.writeToFile("INIT", "Loaded plugins:");

		for (var plugin : KubeJSPlugins.getAll()) {
			scriptType.console.writeToFile("INIT", "- " + plugin.getClass().getName());
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
			scriptType.console.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
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
		if (Files.notExists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try (var out = Files.newOutputStream(directory.resolve("example.js"))) {
				out.write(("""
					// priority: 0

					// Visit the wiki for more info - https://kubejs.com/

					console.info('Hello, World! (Loaded\s""" + scriptType.name + " scripts)')\n\n").getBytes(StandardCharsets.UTF_8));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		var pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (var fileInfo : pack.info.scripts) {
			var scriptSource = (ScriptSource.FromPath) info -> directory.resolve(info.file);
			loadFile(pack, fileInfo, scriptSource);
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	public void load() {
		var remapper = RemappingHelper.getMinecraftRemapper();

		var startAll = System.currentTimeMillis();
		context = Context.enter();
		topLevelScope = context.initStandardObjects();

		CURRENT_CONTEXT.set(context);

		context.setProperty("Type", scriptType);
		context.setProperty("Console", scriptType.console);
		context.setClassShutter(this);
		context.setRemapper(remapper);
		context.setApplicationClassLoader(KubeJS.class.getClassLoader());

		if (MiscPlatformHelper.get().isDataGen()) {
			firstLoad = false;
			scriptType.console.info("Skipping KubeJS script loading (DataGen)");
			return;
		}

		canListenEvents = true;

		var typeWrappers = context.getTypeWrappers();
		// typeWrappers.removeAll();
		var bindingsEvent = new BindingsEvent(this, topLevelScope);
		var customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(this);

		for (var plugin : KubeJSPlugins.getAll()) {
			plugin.registerTypeWrappers(scriptType, typeWrappers);
			plugin.registerBindings(bindingsEvent);
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
				pack.scope = new NativeObject(context);
				pack.scope.setParentScope(topLevelScope);

				for (var file : pack.scripts) {
					t++;
					var start = System.currentTimeMillis();

					try {
						file.load();
						i++;
						scriptType.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} catch (Throwable ex) {
						scriptType.console.handleError(ex, null, "Error loading KubeJS script: " + file.info.location + "'");
					}
				}
			} catch (Throwable ex) {
				scriptType.console.error("Failed to read script pack " + pack.info.namespace + ": ", ex);
			}
		}

		scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + scriptType.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		firstLoad = false;
		canListenEvents = false;
	}

	public NativeJavaClass loadJavaClass(String name, boolean error) {
		if (name == null || name.equals("null") || name.isEmpty()) {
			if (error) {
				throw Context.reportRuntimeError("Class name can't be empty!", context);
			} else {
				return null;
			}
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		var either = javaClassCache.get(name);

		if (either == null) {
			either = Either.right(false);

			if (!isClassAllowed(name)) {
				either = Either.right(true);
			} else {
				try {
					either = Either.left(new NativeJavaClass(context, topLevelScope, Class.forName(name)));
					scriptType.console.info("Loaded Java class '%s'".formatted(name));
				} catch (Exception ignored1) {
					var name1 = RemappingHelper.getMinecraftRemapper().getUnmappedClass(name);

					if (!name1.isEmpty()) {
						if (!isClassAllowed(name1)) {
							either = Either.right(true);
						} else {
							try {
								either = Either.left(new NativeJavaClass(context, topLevelScope, Class.forName(name1)));
								scriptType.console.info("Loaded Java class '%s'".formatted(name));
							} catch (Exception ignored2) {
							}
						}
					}
				}
			}

			javaClassCache.put(name, either);
		}

		var l = either.left().orElse(null);

		if (l != null) {
			return l;
		} else if (error) {
			var found = either.right().orElse(false);
			throw Context.reportRuntimeError((found ? "Failed to load Java class '%s': Class is not allowed by class filter!" : "Failed to load Java class '%s': Class could not be found!").formatted(name), context);
		} else {
			return null;
		}
	}

	@Override
	public boolean visibleToScripts(String fullClassName, int type) {
		return type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isClassAllowed(fullClassName);
	}
}