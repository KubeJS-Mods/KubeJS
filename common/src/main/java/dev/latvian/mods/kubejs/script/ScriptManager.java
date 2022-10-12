package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ScriptManager implements ClassShutter {
	public final ScriptType type;
	public final Path directory;
	private final String exampleScript;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	private Map<String, Optional<NativeJavaClass>> javaClassCache;
	public boolean canListenEvents;

	public ScriptManager(ScriptType t, Path p, String e) {
		type = t;
		directory = p;
		exampleScript = e;
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(type);
	}

	public void unload() {
		packs.clear();
		type.unload();
		javaClassCache = null;
	}

	public void reload(@Nullable ResourceManager resourceManager) {
		unload();
		loadFromDirectory();

		if (resourceManager != null) {
			loadFromResources(resourceManager);
		}

		load();
	}

	private void loadFromResources(ResourceManager resourceManager) {
		Map<String, List<ResourceLocation>> packMap = new HashMap<>();

		for (var resource : resourceManager.listResources("kubejs", s -> s.getPath().endsWith(".js")).keySet()) {
			packMap.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (var entry : packMap.entrySet()) {
			var pack = new ScriptPack(this, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (var id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (var fileInfo : pack.info.scripts) {
				var scriptSource = (ScriptSource.FromResource) info -> resourceManager.getResourceOrThrow(info.id);
				var error = fileInfo.preload(scriptSource);

				if (fileInfo.isIgnored()) {
					continue;
				}

				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					type.console.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}

			pack.scripts.sort(null);
			packs.put(pack.info.namespace, pack);
		}
	}

	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (var in = Files.newInputStream(KubeJS.thisMod.findResource("data", "kubejs", exampleScript).get());
				 var out = Files.newOutputStream(directory.resolve("script.js"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		var pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (var fileInfo : pack.info.scripts) {
			var scriptSource = (ScriptSource.FromPath) info -> directory.resolve(info.file);

			var error = fileInfo.preload(scriptSource);

			var packMode = fileInfo.getPackMode();
			if (fileInfo.isIgnored() || (!packMode.equals("default") && !packMode.equals(CommonProperties.get().packMode))) {
				continue;
			}

			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	public void load() {
		var context = Context.enterWithNewFactory();
		var startAll = System.currentTimeMillis();

		canListenEvents = true;
		var i = 0;
		var t = 0;

		for (var pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();
				SharedContextData contextData = SharedContextData.get(pack.scope);
				contextData.setExtraProperty("Type", type);
				contextData.setExtraProperty("Console", type.console);
				contextData.setClassShutter(this);
				contextData.setRemapper(RemappingHelper.getMinecraftRemapper());
				var typeWrappers = contextData.getTypeWrappers();
				// typeWrappers.removeAll();
				KubeJSPlugins.forEachPlugin(plugin -> plugin.registerTypeWrappers(type, typeWrappers));

				for (var registryTypeWrapperFactory : RegistryTypeWrapperFactory.getAll()) {
					try {
						typeWrappers.register(registryTypeWrapperFactory.type, UtilsJS.cast(registryTypeWrapperFactory));
					} catch (IllegalArgumentException ignored) {
					}
				}

				var bindingsEvent = new BindingsEvent(this, contextData);
				KubeJSPlugins.forEachPlugin(plugin -> plugin.registerBindings(bindingsEvent));

				var customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(this, contextData);
				KubeJSPlugins.forEachPlugin(plugin -> plugin.registerCustomJavaToJsWrappers(customJavaToJsWrappersEvent));

				for (var file : pack.scripts) {
					t++;
					var start = System.currentTimeMillis();

					if (file.load()) {
						i++;
						type.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} else if (file.getError() != null) {
						if (file.getError() instanceof RhinoException) {
							type.console.error("Error loading KubeJS script: " + file.getError().getMessage());
						} else {
							type.console.error("Error loading KubeJS script: " + file.info.location + ": " + file.getError());
							file.getError().printStackTrace();
						}
					}
				}
			} catch (Throwable ex) {
				type.console.error("Failed to read script pack " + pack.info.namespace + ": ", ex);
			}
		}

		type.console.info("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		Context.exit();
		firstLoad = false;
		canListenEvents = false;

		if (i != t && type == ScriptType.STARTUP) {
			throw new RuntimeException("There were startup script syntax errors! See logs/kubejs/startup.txt for more info");
		}
	}

	public NativeJavaClass loadJavaClass(BindingsEvent event, String name0, boolean warn) {
		if (name0 == null || name0.equals("null") || name0.isEmpty()) {
			throw Context.reportRuntimeError("Class name can't be empty!");
		}

		String name = RemappingHelper.getMinecraftRemapper().getUnmappedClass(name0);

		if (name.isEmpty()) {
			name = name0;
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		var ch = javaClassCache.get(name);

		if (ch == null) {
			javaClassCache.put(name, Optional.empty());

			try {
				if (!isClassAllowed(name)) {
					throw Context.reportRuntimeError("Failed to load Java class '%s': Class is not allowed by class filter!".formatted(name));
				}

				var c = Class.forName(name);
				var njc = new NativeJavaClass(event.contextData.topLevelScope, c);
				javaClassCache.put(name, Optional.of(njc));
				event.manager.type.console.pushLineNumber();
				event.manager.type.console.info("Loaded Java class '%s'".formatted(name0));
				event.manager.type.console.popLineNumber();
				return njc;
			} catch (ClassNotFoundException cnf) {
				throw Context.reportRuntimeError("Failed to load Java class '%s': Class could not be found!\n%s".formatted(name, cnf.getMessage()));
			}
		}

		if (ch.isPresent()) {
			return ch.get();
		}

		throw Context.reportRuntimeError("Failed to load Java class '%s'!".formatted(name));
	}

	@Override
	public boolean visibleToScripts(String fullClassName, int type) {
		return type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isClassAllowed(fullClassName);
	}
}