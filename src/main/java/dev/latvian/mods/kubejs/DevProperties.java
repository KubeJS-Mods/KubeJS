package dev.latvian.mods.kubejs;

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
		debugInfo = get("debug_info", !FMLLoader.isProduction());
		dataPackOutput = get("data_pack_output", false);
		logAddedRecipes = get("log_added_recipes", false);
		logRemovedRecipes = get("log_removed_recipes", false);
		logModifiedRecipes = get("log_modified_recipes", false);
		logSkippedRecipes = get("log_skipped_recipes", false);
		logSkippedTags = get("log_skipped_tags", false);
		logErroringRecipes = get("log_erroring_recipes", true);
		logInvalidRecipeHandlers = get("log_invalid_recipe_handlers", true);
		logSkippedPlugins = get("log_skipped_plugins", true);
		logGeneratedData = get("log_generated_data", false);
		strictTags = get("strict_tags", false);
		alwaysCaptureErrors = get("always_capture_errors", false);
	}
}