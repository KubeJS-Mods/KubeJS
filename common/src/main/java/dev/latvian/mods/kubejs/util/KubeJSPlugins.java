package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class KubeJSPlugins {
	private static final List<KubeJSPlugin> LIST = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();

	public static void load(String id, Path path) throws Exception {
		if (Files.isDirectory(path)) {
			var pp = path.resolve("kubejs.plugins.txt");
			if (Files.exists(pp)) {
				loadFromFile(id, Files.readAllLines(pp));
			}

			var pc = path.resolve("kubejs.classfilter.txt");
			if (Files.exists(pc)) {
				GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc));
			}
		} else if (Files.isRegularFile(path) && (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".zip"))) {
			try (var fs = FileSystems.newFileSystem(path, Map.of("create", false))) {
				var pp = fs.getPath("kubejs.plugins.txt");
				if (Files.exists(pp)) {
					loadFromFile(id, Files.readAllLines(pp));
				}

				var pc = path.resolve("kubejs.classfilter.txt");
				if (Files.exists(pc)) {
					GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc));
				}
			}
		}
	}

	private static void loadFromFile(String id, List<String> list) {
		KubeJS.LOGGER.info("Found " + id + " plugin");

		for (var s : list) {
			if (s.trim().isEmpty()) {
				continue;
			}

			try {
				var c = Class.forName(s);

				if (KubeJSPlugin.class.isAssignableFrom(c)) {
					LIST.add((KubeJSPlugin) c.getDeclaredConstructor().newInstance());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
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
