package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.BaseProperties;
import dev.latvian.mods.kubejs.KubeJSPaths;

public class ClientProperties extends BaseProperties {
	private static ClientProperties instance;

	public static ClientProperties get() {
		if (instance == null) {
			instance = new ClientProperties();
		}

		return instance;
	}

	public static void reload() {
		instance = new ClientProperties();
	}

	// private static final ColourScheme.Colour DEFAULT_BACKGROUND_COLOR = new ColourScheme.Colour(46, 52, 64); // #2E3440
	// private static final ColourScheme.Colour DEFAULT_FOREGROUND_COLOR = new ColourScheme.Colour(236, 239, 244); // #ECEFF4

	public String windowTitle;
	public boolean showTagNames;
	public boolean showComponents;
	public boolean disableRecipeBook;
	public boolean disableComponentCountTooltip;
	public boolean disableTabNameTooltip;
	public boolean exportAtlases;
	// public ColourScheme.Colour launchBackgroundColor;
	// public ColourScheme.Colour launchForegroundColor;
	// public int menuBackgroundBrightness;
	// public int menuInnerBackgroundBrightness;
	// public float menuBackgroundScale;
	public boolean blurScaledPackIcon;
	public boolean customStackSizeText;
	public boolean shrinkStackSizeText;

	private ClientProperties() {
		super(KubeJSPaths.CLIENT_PROPERTIES, "KubeJS Client Properties");
	}

	@Override
	protected void load() {
		windowTitle = get("window_title", "");
		showTagNames = get("show_tag_names", true);
		showComponents = get("show_components", true);
		disableRecipeBook = get("disable_recipe_book", false);
		disableComponentCountTooltip = get("disable_component_count_tooltip", true);
		disableTabNameTooltip = get("disable_tab_name_tooltip", false);
		exportAtlases = get("export_atlases", false);
		// launchBackgroundColor = getColor("launch_background_color", DEFAULT_BACKGROUND_COLOR);
		// launchForegroundColor = getColor("launch_foreground_color", DEFAULT_FOREGROUND_COLOR);
		// menuBackgroundBrightness = Mth.clamp(get("menuBackgroundBrightness", 64), 0, 255);
		// menuInnerBackgroundBrightness = Mth.clamp(get("menuInnerBackgroundBrightness", 32), 0, 255);
		// menuBackgroundScale = (float) Mth.clamp(get("menuBackgroundScale", 32D), 0.0625D, 1024D);
		blurScaledPackIcon = get("blur_scaled_pack_icon", true);
		customStackSizeText = get("custom_stack_size_text", true);
		shrinkStackSizeText = get("shrink_stack_size_text", true);
	}

	/*
	public ColourScheme.Colour getColor(String key, ColourScheme.Colour def) {
		var s = get(key, String.format("#%06X", (def.red() << 16) | (def.green() << 8) | def.blue()));

		if (s.isEmpty() || s.equals("default")) {
			return def;
		}

		try {
			int rgb = 0xFFFFFF & Integer.decode(s.charAt(0) == '#' ? s : ("#" + s));
			return new ColourScheme.Colour((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
		} catch (Exception ex) {
			return def;
		}
	}

	public ColourScheme override(ColourScheme original) throws Exception {
		return original;
	}
	 */
}