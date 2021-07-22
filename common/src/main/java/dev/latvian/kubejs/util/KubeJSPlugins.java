package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.ScriptType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KubeJSPlugins {
	private static final List<KubeJSPlugin> LIST = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();

	public static void load(String id, Path path) throws Exception {
		if (Files.isDirectory(path)) {
			Path pp = path.resolve("kubejs.plugins.txt");

			if (Files.exists(pp)) {
				loadFromFile(id, Files.readAllLines(pp));
			}

			Path pc = path.resolve("kubejs.classfilter.txt");

			if (Files.exists(pc)) {
				GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc));
			}
		} else if (Files.isRegularFile(path) && (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".zip"))) {
			ZipFile file = new ZipFile(path.toFile());
			ZipEntry zep = file.getEntry("kubejs.plugins.txt");

			if (zep != null) {
				try (InputStream stream = file.getInputStream(zep);
					 BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), StandardCharsets.UTF_8))) {
					List<String> list = new ArrayList<>();
					String s;

					while ((s = reader.readLine()) != null) {
						list.add(s);
					}

					loadFromFile(id, list);
				}
			}

			ZipEntry zec = file.getEntry("kubejs.classfilter.txt");

			if (zec != null) {
				try (InputStream stream = file.getInputStream(zec);
					 BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), StandardCharsets.UTF_8))) {
					String s;

					while ((s = reader.readLine()) != null) {
						GLOBAL_CLASS_FILTER.add(s);
					}
				}
			}
		}
	}

	private static void loadFromFile(String id, List<String> list) {
		KubeJS.LOGGER.info("Found " + id + " plugin");

		for (String s : list) {
			if (s.trim().isEmpty()) {
				continue;
			}

			try {
				Class<?> c = Class.forName(s);

				if (KubeJSPlugin.class.isAssignableFrom(c)) {
					LIST.add((KubeJSPlugin) c.getDeclaredConstructor().newInstance());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static ClassFilter createClassFilter(ScriptType type) {
		ClassFilter filter = new ClassFilter();
		forEachPlugin(plugin -> plugin.addClasses(type, filter));

		for (String s : GLOBAL_CLASS_FILTER) {
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
