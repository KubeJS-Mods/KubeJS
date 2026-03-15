package dev.latvian.mods.kubejs.plugin;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ModResourceBindings;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class KubeJSPlugins {
	private static final List<KubeJSPlugin> LIST = new ArrayList<>();
	private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();
	private static final ModResourceBindings BINDINGS = new ModResourceBindings();

	public static void load(List<IModFile> modFiles, boolean loadClientPlugins) {
		try {
			for (var file : modFiles) {
				if (!file.getModInfos().isEmpty()) {
					loadMod(file.getModInfos().getFirst().getModId(), file, loadClientPlugins);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load KubeJS plugin", ex);
		}
	}

	private static void loadMod(String modId, IModFile mod, boolean loadClientPlugins) throws IOException {
		var contents = mod.getContents();

		var pluginData = contents.readFile("kubejs.plugins.txt");
		if (pluginData != null) {
			loadFromFile(new String(pluginData, StandardCharsets.UTF_8).lines(), modId, loadClientPlugins);
		}

		var filterData = contents.readFile("kubejs.classfilter.txt");
		if (filterData != null) {
			GLOBAL_CLASS_FILTER.addAll(new String(filterData, StandardCharsets.UTF_8).lines().toList());
		}

		BINDINGS.readBindings(modId, mod);
	}

	private static void loadFromFile(Stream<String> contents, String source, boolean loadClientPlugins) {
		KubeJS.LOGGER.info("Found plugin source {}", source);

		contents.map(s -> s.split("#", 2)[0].trim()) // allow comments (#)
			.filter(s -> !s.isBlank()) // filter empty lines
			.flatMap(s -> {
				String[] line = s.split(" ");

				for (int i = 1; i < line.length; i++) {
					if (line[i].equalsIgnoreCase("client")) {
						if (!loadClientPlugins) {
							if (DevProperties.get().logSkippedPlugins) {
								KubeJS.LOGGER.warn("Plugin " + line[0] + " does not load on server side, skipping");
							}

							return Stream.empty();
						}
					} else if (!ModList.get().isLoaded(line[i])) {
						if (DevProperties.get().logSkippedPlugins) {
							KubeJS.LOGGER.warn("Plugin " + line[0] + " does not have required mod '" + line[i] + "' loaded, skipping");
						}

						return Stream.empty();
					}
				}

				try {
					return Stream.of(Class.forName(line[0])); // try to load plugin class
				} catch (Throwable t) {
					KubeJS.LOGGER.error("Failed to load plugin {} from source {}: {}", s, source, t);
					t.printStackTrace();
					return Stream.empty();
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
		var filter = new ClassFilter(type);
		forEachPlugin(filter, KubeJSPlugin::registerClasses);

		for (var s : GLOBAL_CLASS_FILTER) {
			if (s.length() >= 2) {
				if (s.startsWith("+")) {
					filter.allow(s.substring(1).trim());
				} else if (s.startsWith("-")) {
					filter.deny(s.substring(1).trim());
				}
			}
		}

		return filter;
	}

	public static void forEachPlugin(Consumer<KubeJSPlugin> callback) {
		LIST.forEach(callback);
	}

	public static <T> void forEachPlugin(T instance, BiConsumer<KubeJSPlugin, T> callback) {
		for (var item : LIST) {
			callback.accept(item, instance);
		}
	}

	public static List<KubeJSPlugin> getAll() {
		return Collections.unmodifiableList(LIST);
	}

	public static void addSidedBindings(BindingRegistry event) {
		BINDINGS.addBindings(event);
	}
}
