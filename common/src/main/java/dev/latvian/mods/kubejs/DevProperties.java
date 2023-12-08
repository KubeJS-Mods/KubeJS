package dev.latvian.mods.kubejs;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Properties;

public class DevProperties {
	private static DevProperties instance;

	public static DevProperties get() {
		if (instance == null) {
			instance = new DevProperties();
		}

		return instance;
	}

	public static void reload() {
		instance = new DevProperties();
	}

	private final Properties properties;
	private boolean writeProperties;

	public boolean debugInfo;
	public boolean dataPackOutput = false;
	public boolean logAddedRecipes = false;
	public boolean logRemovedRecipes = false;
	public boolean logModifiedRecipes = false;
	public boolean logSkippedRecipes = false;
	public boolean logSkippedTags = false;
	public boolean logErroringRecipes = true;
	public boolean logInvalidRecipeHandlers = true;
	public boolean logSkippedPlugins = true;
	public boolean logGeneratedData = false;
	public boolean strictTags = false;
	public boolean alwaysCaptureErrors = false;

	private DevProperties() {
		properties = new Properties();

		try {
			var propertiesFile = KubeJSPaths.getLocalDevProperties();
			writeProperties = false;

			if (Files.exists(propertiesFile)) {
				try (Reader reader = Files.newBufferedReader(propertiesFile)) {
					properties.load(reader);
				}
			} else {
				writeProperties = true;
			}

			debugInfo = get("debugInfo", Platform.isDevelopmentEnvironment());
			dataPackOutput = get("dataPackOutput", false);
			logAddedRecipes = get("logAddedRecipes", false);
			logRemovedRecipes = get("logRemovedRecipes", false);
			logModifiedRecipes = get("logModifiedRecipes", false);
			logSkippedRecipes = get("logSkippedRecipes", false);
			logSkippedTags = get("logSkippedTags", false);
			logErroringRecipes = get("logErroringRecipes", true);
			logInvalidRecipeHandlers = get("logInvalidRecipeHandlers", true);
			logSkippedPlugins = get("logSkippedPlugins", true);
			logGeneratedData = get("logGeneratedData", false);
			strictTags = get("strictTags", false);
			alwaysCaptureErrors = get("alwaysCaptureErrors", false);

			KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::loadDevProperties);

			if (writeProperties) {
				save();
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

	public void save() {
		try (Writer writer = Files.newBufferedWriter(KubeJSPaths.getLocalDevProperties())) {
			properties.store(writer, "KubeJS Dev Properties");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}