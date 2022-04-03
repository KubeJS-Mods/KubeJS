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
import dev.latvian.mods.rhino.Scriptable;
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
		context.setClassShutter((fullClassName, type) -> type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isClassAllowed(fullClassName));
		context.setRemapper(RemappingHelper.createModRemapper());
		var typeWrappers = context.getTypeWrappers();
		// typeWrappers.removeAll();
		KubeJSPlugins.forEachPlugin(plugin -> plugin.addTypeWrappers(type, typeWrappers));

		for (var registryTypeWrapperFactory : RegistryTypeWrapperFactory.getAll()) {
			try {
				typeWrappers.register(registryTypeWrapperFactory.type, UtilsJS.cast(registryTypeWrapperFactory));
			} catch (IllegalArgumentException ignored) {
			}
		}

		var startAll = System.currentTimeMillis();

		var i = 0;
		var t = 0;

		for (var pack : packs.values()) {
			try {
				pack.context = context;
				pack.scope = context.initStandardObjects();

				var event = new BindingsEvent(this, pack.context, pack.scope);
				KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));
				BindingsEvent.EVENT.invoker().accept(event);

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

	public NativeJavaClass loadJavaClass(Scriptable scope, Object[] args) {
		String name = RemappingHelper.getMinecraftRemapper().getUnmappedClass(args[0].toString());

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
					throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
				}

				var c = Class.forName(name);
				var njc = new NativeJavaClass(scope, c);
				javaClassCache.put(name, Optional.of(njc));
				return njc;
			} catch (Throwable ex) {
				throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
			}
		}

		if (ch.isPresent()) {
			return ch.get();
		}

		throw Context.reportRuntimeError("Class '" + name + "' is not allowed!");
	}
}