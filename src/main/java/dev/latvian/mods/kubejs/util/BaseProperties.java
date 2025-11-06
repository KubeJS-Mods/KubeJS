package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;

import java.io.IOException;
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

		writeProperties = false;

		if (Files.exists(path)) {
			try (var reader = Files.newBufferedReader(path)) {
				properties = GSON.fromJson(reader, JsonObject.class);
			} catch (IOException | JsonIOException e) {
				CrashReport crashreport = CrashReport.forThrowable(e, "[KubeJS] Loading settings");
				CrashReportCategory crashreportcategory = crashreport.addCategory("File being loaded");
				crashreportcategory.setDetail("Name", () -> name);
				crashreportcategory.setDetail("File Path", path::toString);
				crashreportcategory.setDetail("Readable?", () -> String.valueOf(Files.isReadable(path)));
				crashreportcategory.setDetail("Writable?", () -> String.valueOf(Files.isWritable(path)));
				crashreportcategory.setDetail("Executable?", () -> String.valueOf(Files.isExecutable(path)));

				throw new ReportedException(crashreport);
			} catch (JsonSyntaxException e) {
				ConsoleJS.STARTUP.error("Error parsing properties JSON for file %s! Default settings will be loaded, and changes won't be saved!".formatted(path), e);
			}
		} else {
			writeProperties = true;
		}

		load();

		if (writeProperties) {
			save();
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

		if (s instanceof JsonNull) {
			properties.addProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s.getAsString();
	}

	public JsonElement get(String key, JsonElement def) {
		var s = get(key);

		if (s instanceof JsonNull) {
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
			ConsoleJS.STARTUP.error("Error saving properties file %s! Settings will not be saved!".formatted(path), ex);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}