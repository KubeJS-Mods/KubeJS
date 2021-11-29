package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonUtilsJS {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

	public static JsonElement copy(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return JsonNull.INSTANCE;
		} else if (element instanceof JsonArray) {
			JsonArray a = new JsonArray();

			for (JsonElement e : (JsonArray) element) {
				a.add(copy(e));
			}

			return a;
		} else if (element instanceof JsonObject) {
			JsonObject o = new JsonObject();

			for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
				o.add(entry.getKey(), copy(entry.getValue()));
			}

			return o;
		}

		return element;
	}

	public static JsonElement of(@Nullable Object o) {
		if (o == null) {
			return JsonNull.INSTANCE;
		} else if (o instanceof JsonSerializable) {
			return ((JsonSerializable) o).toJson();
		} else if (o instanceof JsonElement) {
			return (JsonElement) o;
		} else if (o instanceof CharSequence) {
			return new JsonPrimitive(o.toString());
		} else if (o instanceof Boolean) {
			return new JsonPrimitive((Boolean) o);
		} else if (o instanceof Number) {
			return new JsonPrimitive((Number) o);
		} else if (o instanceof Character) {
			return new JsonPrimitive((Character) o);
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public static Object toObject(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return null;
		} else if (json.isJsonObject()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			JsonObject o = json.getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
				map.put(entry.getKey(), toObject(entry.getValue()));
			}

			return map;
		} else if (json.isJsonArray()) {
			JsonArray a = json.getAsJsonArray();
			List<Object> objects = new ArrayList<>(a.size());

			for (JsonElement e : a) {
				objects.add(toObject(e));
			}

			return objects;
		}

		return toPrimitive(json);
	}

	public static String toString(JsonElement json) {
		StringWriter writer = new StringWriter();

		try {
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			jsonWriter.setHtmlSafe(false);
			Streams.write(json, jsonWriter);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public static String toPrettyString(JsonElement json) {
		StringWriter writer = new StringWriter();

		try {
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			jsonWriter.setHtmlSafe(false);
			Streams.write(json, jsonWriter);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public static JsonElement fromString(@Nullable String string) {
		if (string == null || string.isEmpty() || string.equals("null")) {
			return JsonNull.INSTANCE;
		}

		try {
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			JsonElement element;
			boolean lenient = jsonReader.isLenient();
			jsonReader.setLenient(true);
			element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return element;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public static Object toPrimitive(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return null;
		} else if (element.isJsonPrimitive()) {
			JsonPrimitive p = element.getAsJsonPrimitive();

			if (p.isBoolean()) {
				return p.getAsBoolean();
			} else if (p.isNumber()) {
				return p.getAsNumber();
			}

			try {
				Double.parseDouble(p.getAsString());
				return p.getAsNumber();
			} catch (Exception ex) {
				return p.getAsString();
			}
		}

		return null;
	}

	@Nullable
	public static MapJS read(File file) throws IOException {
		KubeJS.verifyFilePath(file);

		if (!file.exists()) {
			return null;
		}

		try (FileReader fileReader = new FileReader(file);
			 JsonReader jsonReader = new JsonReader(fileReader)) {
			JsonElement element;
			boolean lenient = jsonReader.isLenient();
			jsonReader.setLenient(true);
			element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return MapJS.of(element);
		}
	}

	public static void write(File file, @Nullable MapJS o) throws IOException {
		KubeJS.verifyFilePath(file);

		if (o == null) {
			file.delete();
			return;
		}

		JsonObject json = o.toJson();

		try (Writer fileWriter = new FileWriter(file);
			 JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(fileWriter))) {
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
	}

	@Nullable
	public static MapJS read(String file) throws IOException {
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

	public static void write(String file, @Nullable MapJS json) throws IOException {
		write(KubeJS.getGameDirectory().resolve(file).toFile(), json);
	}

	public static JsonArray toArray(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}

		JsonArray a = new JsonArray();
		a.add(element);
		return a;
	}
}