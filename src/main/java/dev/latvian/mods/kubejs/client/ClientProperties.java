package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.BaseProperties;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.util.Mth;

import java.util.OptionalInt;

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

	public String title;
	private boolean showTagNames;
	private boolean disableRecipeBook;
	private boolean exportAtlases;
	private boolean overrideColors;
	private int backgroundColor;
	private int barColor;
	private int barBorderColor;
	private float[] backgroundColor3f;
	private float[] fmlMemoryColor3f;
	private float[] fmlLogColor3f;
	private int menuBackgroundBrightness;
	private int menuInnerBackgroundBrightness;
	private float menuBackgroundScale;
	public boolean blurScaledPackIcon;

	private ClientProperties() {
		super(KubeJSPaths.CLIENT_PROPERTIES, "KubeJS Client Properties");
	}

	@Override
	protected void load() {
		title = get("title", "");
		showTagNames = get("showTagNames", false);
		disableRecipeBook = get("disableRecipeBook", false);
		exportAtlases = get("exportAtlases", false);
		overrideColors = get("overrideColors", false);
		backgroundColor = getColor("backgroundColor", 0x2E3440);
		barColor = getColor("barColor", 0xECEFF4);
		barBorderColor = getColor("barBorderColor", 0xECEFF4);
		backgroundColor3f = getColor3f(backgroundColor);
		fmlMemoryColor3f = getColor3f(getColor("fmlMemoryColor", 0xECEFF4));
		fmlLogColor3f = getColor3f(getColor("fmlLogColor", 0xECEFF4));

		menuBackgroundBrightness = Mth.clamp(get("menuBackgroundBrightness", 64), 0, 255);
		menuInnerBackgroundBrightness = Mth.clamp(get("menuInnerBackgroundBrightness", 32), 0, 255);
		menuBackgroundScale = (float) Mth.clamp(get("menuBackgroundScale", 32D), 0.0625D, 1024D);
		blurScaledPackIcon = get("blurScaledPackIcon", true);

		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::loadClientProperties);
	}

	public boolean getShowTagNames() {
		return showTagNames;
	}

	public boolean getDisableRecipeBook() {
		return disableRecipeBook;
	}

	public boolean getExportAtlases() {
		return exportAtlases;
	}

	public float[] getMemoryColor(float[] color) {
		return overrideColors ? fmlMemoryColor3f : color;
	}

	public float[] getLogColor(float[] color) {
		return overrideColors ? fmlLogColor3f : color;
	}

	public OptionalInt getBackgroundColor() {
		return overrideColors ? OptionalInt.of(0xFF000000 | backgroundColor) : OptionalInt.empty();
	}

	public int getBarColor(int color) {
		return overrideColors ? ((color & 0xFF000000) | barColor) : color;
	}

	public int getBarBorderColor(int color) {
		return overrideColors ? ((color & 0xFF000000) | barBorderColor) : color;
	}

	public int getMenuBackgroundBrightness() {
		return menuBackgroundBrightness;
	}

	public int getMenuInnerBackgroundBrightness() {
		return menuInnerBackgroundBrightness;
	}

	public float getMenuBackgroundScale() {
		return menuBackgroundScale;
	}
}