package dev.latvian.mods.kubejs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BaseProperties {
	private final Path path;
	private final String name;
	protected final Properties properties;
	private boolean writeProperties;

	protected BaseProperties(Path path, String name) {
		this.path = path;
		this.name = name;
		this.properties = new Properties();

		try {
			writeProperties = false;

			if (Files.exists(path)) {
				try (var reader = Files.newBufferedReader(path)) {
					properties.load(reader);
				}
			} else {
				writeProperties = true;
			}

			load();

			if (writeProperties) {
				save();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		KubeJS.LOGGER.info("Loaded " + path.getFileName());
	}

	protected void load() {
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

	public void save() {
		try (var writer = Files.newBufferedWriter(path)) {
			properties.store(writer, name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}