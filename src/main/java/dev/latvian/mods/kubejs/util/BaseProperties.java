package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.nio.file.Files;
import java.nio.file.Path;

public class BaseProperties {
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();

	private final Path path;
	private final String name;
	protected JsonObject properties;
	private boolean writeProperties;

	public BaseProperties(Path path, String name) {
		this.path = path;
		this.name = name;
		this.properties = new JsonObject();

		try {
			writeProperties = false;

			if (Files.exists(path)) {
				try (var reader = Files.newBufferedReader(path)) {
					properties = GSON.fromJson(reader, JsonObject.class);
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
	}

	protected void load() {
	}

	public void remove(String key) {
		if (properties.remove(key) != null) {
			writeProperties = true;
		}
	}

	public JsonElement get(String key) {
		var e = properties.get(key);
		return e == null ? JsonNull.INSTANCE : e;
	}

	public String get(String key, String def) {
		var s = get(key);

		if (s.isJsonNull()) {
			properties.addProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s.getAsString();
	}

	public JsonElement get(String key, JsonElement def) {
		var s = get(key);

		if (s.isJsonNull()) {
			properties.add(key, def);
			writeProperties = true;
			return def;
		}

		return s;
	}

	public boolean get(String key, boolean def) {
		return get(key, new JsonPrimitive(def)).getAsBoolean();
	}

	public int get(String key, int def) {
		return get(key, new JsonPrimitive(def)).getAsInt();
	}

	public double get(String key, double def) {
		return get(key, new JsonPrimitive(def)).getAsDouble();
	}

	public void set(String key, JsonElement json) {
		properties.add(key, json);
		writeProperties = true;
	}

	public void save() {
		try (var writer = Files.newBufferedWriter(path)) {
			GSON.toJson(properties, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return name;
	}
}