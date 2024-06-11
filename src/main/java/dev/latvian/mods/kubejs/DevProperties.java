package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.neoforged.fml.loading.FMLLoader;

public class DevProperties extends BaseProperties {
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
		super(KubeJSPaths.getLocalDevProperties(), "KubeJS Dev Properties");
	}

	@Override
	protected void load() {
		debugInfo = get("debugInfo", !FMLLoader.isProduction());
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
	}
}