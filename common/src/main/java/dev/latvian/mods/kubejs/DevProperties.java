package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.util.KubeJSPlugins;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author LatvianModder
 */
public class DevProperties {
	private static DevProperties instance;

	public static DevProperties get() {
		if (instance == null) {
			instance = new DevProperties();
		}

		return instance;
	}

	private final Properties properties;
	private boolean writeProperties;

	public boolean dataPackOutput = false;
	public boolean logAddedRecipes = false;
	public boolean logRemovedRecipes = false;
	public boolean logModifiedRecipes = false;
	public boolean logOverrides = false;
	public boolean logSkippedRecipes = false;
	public boolean logSkippedTags = false;
	public boolean logErroringRecipes = true;
	public boolean logInvalidRecipeHandlers = true;
	public boolean logSkippedPlugins = false;

	private DevProperties() {
		properties = new Properties();

		try {
			var propertiesFile = CommonProperties.get().saveDevPropertiesInConfig ? KubeJSPaths.CONFIG.resolve("dev.properties") : KubeJSPaths.LOCAL.resolve("kubejsdev.properties");
			writeProperties = false;

			if (Files.exists(propertiesFile)) {
				try (Reader reader = Files.newBufferedReader(propertiesFile)) {
					properties.load(reader);
				}
			} else {
				writeProperties = true;
			}

			dataPackOutput = get("dataPackOutput", false);
			logAddedRecipes = get("logAddedRecipes", false);
			logRemovedRecipes = get("logRemovedRecipes", false);
			logModifiedRecipes = get("logModifiedRecipes", false);
			logOverrides = get("logOverrides", false);
			logSkippedRecipes = get("logSkippedRecipes", false);
			logSkippedTags = get("logSkippedTags", false);
			logErroringRecipes = get("logErroringRecipes", true);
			logInvalidRecipeHandlers = get("logInvalidRecipeHandlers", true);
			logSkippedPlugins = get("logSkippedPlugins", true);

			KubeJSPlugins.forEachPlugin(p -> p.loadDevProperties(this));

			if (writeProperties) {
				try (Writer writer = Files.newBufferedWriter(propertiesFile)) {
					properties.store(writer, "KubeJS Dev Properties");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		KubeJS.LOGGER.info("Loaded dev.properties");
	}

	public void remove(String key) {
		var s = properties.getProperty(key);

		if (s != null) {
			properties.remove(key);
			writeProperties = true;
		}
	}

	public String get(String key, String def) {
		var s = properties.getProperty(key);

		if (s == null) {
			properties.setProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s;
	}

	public boolean get(String key, boolean def) {
		return get(key, def ? "true" : "false").equals("true");
	}
}