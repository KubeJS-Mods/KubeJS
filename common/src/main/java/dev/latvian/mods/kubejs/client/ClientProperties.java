package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.util.Mth;

import java.io.Reader;
import java.nio.file.Files;
import java.util.OptionalInt;
import java.util.Properties;

public class ClientProperties {
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

	private final Properties properties;
	private boolean writeProperties;
	private final boolean tempIconCancel = true;

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

	private ClientProperties() {
		properties = new Properties();

		try {
			writeProperties = false;

			if (Files.exists(KubeJSPaths.CLIENT_PROPERTIES)) {
				try (Reader reader = Files.newBufferedReader(KubeJSPaths.CLIENT_PROPERTIES)) {
					properties.load(reader);
				}
			} else {
				writeProperties = true;
			}

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

			KubeJSPlugins.forEachPlugin(p -> p.loadClientProperties(this));

			if (writeProperties) {
				save();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		KubeJS.LOGGER.info("Loaded client.properties");
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

	public int get(String key, int def) {
		return Integer.parseInt(get(key, Integer.toString(def)));
	}

	public double get(String key, double def) {
		return Double.parseDouble(get(key, Double.toString(def)));
	}

	public int getColor(String key, int def) {
		var s = get(key, String.format("%06X", def & 0xFFFFFF));

		if (s.isEmpty() || s.equals("default")) {
			return def;
		}

		try {
			return 0xFFFFFF & Integer.decode(s.startsWith("#") ? s : ("#" + s));
		} catch (Exception ex) {
			ex.printStackTrace();
			return def;
		}
	}

	public float[] getColor3f(int color) {
		var c = new float[3];
		c[0] = ((color >> 16) & 0xFF) / 255F;
		c[1] = ((color >> 8) & 0xFF) / 255F;
		c[2] = ((color >> 0) & 0xFF) / 255F;
		return c;
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

	public void save() {
		try (var writer = Files.newBufferedWriter(KubeJSPaths.CLIENT_PROPERTIES)) {
			properties.store(writer, "KubeJS Client Properties");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}