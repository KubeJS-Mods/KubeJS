package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KubeJSPlugins {
	public static final List<KubeJSPlugin> LIST = new ArrayList<>();

	public static void load(String id, Path path) throws Exception {
		if (Files.isDirectory(path)) {
			Path p = path.resolve("kubejs.plugins.txt");

			if (Files.exists(p)) {
				loadFromFile(id, Files.readAllLines(p));
			}
		} else if (Files.isRegularFile(path) && (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".zip"))) {
			ZipFile file = new ZipFile(path.toFile());
			ZipEntry ze = file.getEntry("kubejs.plugins.txt");

			if (ze != null) {
				try (InputStream stream = file.getInputStream(ze);
					 BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), StandardCharsets.UTF_8))) {
					List<String> list = new ArrayList<>();
					String s;

					while ((s = reader.readLine()) != null) {
						list.add(s);
					}

					loadFromFile(id, list);
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
					LIST.add((KubeJSPlugin) c.newInstance());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
