package dev.latvian.mods.kubejs.util;

import dev.architectury.platform.Mod;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class KubeJSPlugins {
	private static final List<KubeJSPlugin> LIST = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();

	public static void load(Mod mod) throws IOException {
		var pp = mod.findResource("kubejs.plugins.txt");
		if (pp.isPresent()) {
			loadFromFile(Files.lines(pp.get()), mod.getModId());
		}

		var pc = mod.findResource("kubejs.classfilter.txt");
		if (pc.isPresent()) {
			GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc.get()));
		}
	}

	private static void loadFromFile(Stream<String> contents, String source) {
		KubeJS.LOGGER.info("Found plugin source {}", source);

		contents.map(s -> s.split("#", 2)[0].trim()) // allow comments (#)
				.filter(s -> !s.isBlank()) // filter empty lines
				.flatMap(s -> {
					try {
						return Stream.of(Class.forName(s)); // try to load plugin class
					} catch (Throwable t) {
						KubeJS.LOGGER.error("Failed to load plugin {} from source {}: {}", s, source, t);
						return null;
					}
				})
				.filter(KubeJSPlugin.class::isAssignableFrom)
				.forEach(c -> {
					try {
						LIST.add((KubeJSPlugin) c.getDeclaredConstructor().newInstance()); // create the actual plugin instance
					} catch (Throwable t) {
						KubeJS.LOGGER.error("Failed to init KubeJS plugin {} from source {}: {}", c.getName(), source, t);
					}
				});
	}

	public static ClassFilter createClassFilter(ScriptType type) {
		var filter = new ClassFilter();
		forEachPlugin(plugin -> plugin.registerClasses(type, filter));

		for (var s : GLOBAL_CLASS_FILTER) {
			if (s.length() >= 2) {
				if (s.startsWith("+")) {
					filter.allow(s.substring(1));
				} else if (s.startsWith("-")) {
					filter.deny(s.substring(1));
				}
			}
		}

		return filter;
	}

	public static void forEachPlugin(Consumer<KubeJSPlugin> callback) {
		LIST.forEach(callback);
	}
}
