package dev.latvian.mods.kubejs;

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

	public boolean dataPackOutput;
	public boolean logRegistryTypes;
	public boolean logRegistryEventObjects;
	public boolean logAddedRecipes;
	public boolean logRemovedRecipes;
	public boolean logModifiedRecipes;
	public boolean logSkippedRecipes;
	public boolean logRecipeDebug;
	public boolean logSkippedTags;
	public boolean logErroringRecipes;
	public boolean logInvalidRecipeHandlers;
	public boolean logSkippedPlugins;
	public boolean logGeneratedData;
	public boolean logEventErrorStackTrace;
	public boolean strictTags;
	public boolean alwaysCaptureErrors;
	public String kubedexSound;

	private DevProperties() {
		super(KubeJSPaths.getLocalDevProperties(), "KubeJS Dev Properties");
	}

	@Override
	protected void load() {
		dataPackOutput = get("data_pack_output", false);
		logRegistryTypes = get("log_registry_types", false);
		logRegistryEventObjects = get("log_registry_event_objects", false);
		logAddedRecipes = get("log_added_recipes", false);
		logRemovedRecipes = get("log_removed_recipes", false);
		logModifiedRecipes = get("log_modified_recipes", false);
		logSkippedRecipes = get("log_skipped_recipes", false);
		logRecipeDebug = get("log_recipe_debug", false);
		logSkippedTags = get("log_skipped_tags", false);
		logErroringRecipes = get("log_erroring_recipes", true);
		logInvalidRecipeHandlers = get("log_invalid_recipe_handlers", true);
		logSkippedPlugins = get("log_skipped_plugins", true);
		logGeneratedData = get("log_generated_data", false);
		logEventErrorStackTrace = get("log_event_error_stack_trace", false);
		strictTags = get("strict_tags", false);
		alwaysCaptureErrors = get("always_capture_errors", false);
		kubedexSound = get("kubedex_sound", "entity.experience_orb.pickup");
	}
}