package dev.latvian.mods.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

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
	public JsonElement creativeModeTabIcon;
	public JsonElement creativeModeTabName;

	private CommonProperties() {
		super(KubeJSPaths.COMMON_PROPERTIES, "KubeJS Common Properties");
	}

	@Override
	protected void load() {
		hideServerScriptErrors = get("hide_server_script_errors", false);
		serverOnly = get("server_only", false);
		announceReload = get("announce_reload", true);
		packMode = get("packmode", "");
		saveDevPropertiesInConfig = get("save_dev_properties_in_config", false);
		allowAsyncStreams = get("allow_async_streams", true);
		matchJsonRecipes = get("match_json_recipes", true);
		ignoreCustomUniqueRecipeIds = get("ignore_custom_unique_recipe_ids", false);
		startupErrorGUI = get("startup_error_gui", true);
		startupErrorReportUrl = get("startup_error_report_url", "");

		creativeModeTabIcon = get("creative_mode_tab_icon", new JsonObject());
		creativeModeTabName = get("creative_mode_tab_name", JsonNull.INSTANCE);
	}

	public void setPackMode(String s) {
		packMode = s;
		set("packmode", new JsonPrimitive(s));
		save();
	}

	public Component getCreativeModeTabName() {
		if (!creativeModeTabName.isJsonNull()) {
			return ComponentSerialization.CODEC.decode(RegistryAccessContainer.BUILTIN.json(), creativeModeTabName).result().map(Pair::getFirst).orElse(KubeJS.NAME_COMPONENT);
		}

		return KubeJS.NAME_COMPONENT;
	}
}