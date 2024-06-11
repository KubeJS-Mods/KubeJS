package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.util.KubeJSPlugins;

public class CommonProperties extends BaseProperties {
	private static CommonProperties instance;

	public static CommonProperties get() {
		if (instance == null) {
			instance = new CommonProperties();
		}

		return instance;
	}

	public static void reload() {
		instance = new CommonProperties();
	}

	public boolean hideServerScriptErrors;
	public boolean serverOnly;
	public boolean announceReload;
	public String packMode;
	public boolean saveDevPropertiesInConfig;
	public boolean allowAsyncStreams;
	public boolean matchJsonRecipes;
	public boolean ignoreCustomUniqueRecipeIds;
	public boolean startupErrorGUI;
	public String startupErrorReportUrl;
	public String creativeModeTabIcon;

	private CommonProperties() {
		super(KubeJSPaths.COMMON_PROPERTIES, "KubeJS Common Properties");
	}

	@Override
	protected void load() {
		hideServerScriptErrors = get("hideServerScriptErrors", false);
		serverOnly = get("serverOnly", false);
		announceReload = get("announceReload", true);
		packMode = get("packmode", "");
		saveDevPropertiesInConfig = get("saveDevPropertiesInConfig", false);
		allowAsyncStreams = get("allowAsyncStreams", true);
		matchJsonRecipes = get("matchJsonRecipes", true);
		ignoreCustomUniqueRecipeIds = get("ignoreCustomUniqueRecipeIds", false);
		startupErrorGUI = get("startupErrorGUI", true);
		startupErrorReportUrl = get("startupErrorReportUrl", "");
		creativeModeTabIcon = get("creativeModeTabIcon", "minecraft:purple_dye");

		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::loadCommonProperties);
	}

	public void setPackMode(String s) {
		packMode = s;
		properties.setProperty("packmode", s);
		save();
	}
}