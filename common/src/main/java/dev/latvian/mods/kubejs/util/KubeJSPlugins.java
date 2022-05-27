package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class KubeJSPlugins {
	private static final List<KubeJSPlugin> LIST = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();

	public static void load(Path path, String source) throws IOException {
		if (Files.isDirectory(path)) {
			loadFromPath(path::resolve, source);
		} else if (Files.isRegularFile(path) && (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".zip"))) {
			try (var fs = FileSystems.newFileSystem(path, Map.of("create", false))) {
				loadFromPath(fs::getPath, source);
			}
		}
	}

	private static <T> void loadFromPath(Function<String, Path> resolver, String source) throws IOException {
		var pp = resolver.apply("kubejs.plugins.txt");
		if (Files.exists(pp)) {
			loadFromFile(Files.lines(pp), source);
		}

		var pc = resolver.apply("kubejs.classfilter.txt");
		if (Files.exists(pc)) {
			GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc));
		}
	}

	private static void loadFromFile(Stream<String> contents, String source) {
		KubeJS.LOGGER.info("Found plugin source {}", source);

		contents.filter(s -> !s.trim().isBlank()) // ignore empty lines
				.filter(s -> !s.startsWith("#")) // allow comments
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
		forEachPlugin(plugin -> plugin.addClasses(type, filter));

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
