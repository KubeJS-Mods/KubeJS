package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.event.EventsJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.commons.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ScriptManager {
	public final ScriptType type;
	public final Path directory;
	public final String exampleScript;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	private Map<String, Optional<NativeJavaClass>> javaClassCache;

	public ScriptManager(ScriptType t, Path p, String e) {
		type = t;
		directory = p;
		exampleScript = e;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(type);
	}

	public void unload() {
		events.clear();
		packs.clear();
		type.errors.clear();
		type.warnings.clear();
		type.console.resetFile();
		javaClassCache = null;
	}

	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (var in = KubeJS.class.getResourceAsStream(exampleScript);
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

		var i = 0;
		var t = 0;

		for (var pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();

				var contextData = SharedContextData.get(pack.scope);
				contextData.setClassShutter((fullClassName, type) -> type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isClassAllowed(fullClassName));
				contextData.setRemapper(RemappingHelper.getMinecraftRemapper());
				var typeWrappers = contextData.getTypeWrappers();
				// typeWrappers.removeAll();
				KubeJSPlugins.forEachPlugin(plugin -> plugin.addTypeWrappers(type, typeWrappers));

				for (var registryTypeWrapperFactory : RegistryTypeWrapperFactory.getAll()) {
					try {
						typeWrappers.register(registryTypeWrapperFactory.type, UtilsJS.cast(registryTypeWrapperFactory));
					} catch (IllegalArgumentException ignored) {
					}
				}

				var bindingsEvent = new BindingsEvent(this, contextData);
				KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(bindingsEvent));
				BindingsEvent.EVENT.invoker().accept(bindingsEvent);

				var customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(this, contextData);
				KubeJSPlugins.forEachPlugin(plugin -> plugin.addCustomJavaToJsWrappers(customJavaToJsWrappersEvent));
				CustomJavaToJsWrappersEvent.EVENT.invoker().accept(customJavaToJsWrappersEvent);

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

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new StartupEventJS());

		if (i != t && type == ScriptType.STARTUP) {
			throw new RuntimeException("There were startup script syntax errors! See logs/kubejs/startup.txt for more info");
		}

		firstLoad = false;
	}

	public NativeJavaClass loadJavaClass(BindingsEvent event, Object[] args) {
		String name = RemappingHelper.getMinecraftRemapper().getUnmappedClass(String.valueOf(Context.jsToJava(event.contextData, args[0], String.class)));

		if (name.isEmpty()) {
			throw Context.reportRuntimeError("Class name can't be empty!");
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		var ch = javaClassCache.get(name);

		if (ch == null) {
			javaClassCache.put(name, Optional.empty());

			try {
				if (!isClassAllowed(name)) {
					throw Context.reportRuntimeError("Failed to dynamically load class '%s': Class is not allowed by class filter!".formatted(name));
				}

				var c = Class.forName(name);
				var njc = new NativeJavaClass(event.contextData.topLevelScope, c);
				javaClassCache.put(name, Optional.of(njc));
				return njc;
			} catch (ClassNotFoundException cnf) {
				throw Context.reportRuntimeError("Failed to dynamically load class '%s': Class could not be found!\n%s".formatted(name, cnf.getMessage()));
			}
		}

		if (ch.isPresent()) {
			return ch.get();
		}

		throw Context.reportRuntimeError("Failed to dynamically load class '%s'!".formatted(name));
	}
}