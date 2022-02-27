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
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author LatvianModder
 */
public class JsonIO {
	public static final transient Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

	public static JsonElement copy(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return JsonNull.INSTANCE;
		} else if (element instanceof JsonArray jsonArr) {
			var a = new JsonArray();

			for (var e : jsonArr) {
				a.add(copy(e));
			}

			return a;
		} else if (element instanceof JsonObject jsonObj) {
			var o = new JsonObject();

			for (var entry : jsonObj.entrySet()) {
				o.add(entry.getKey(), copy(entry.getValue()));
			}

			return o;
		}

		return element;
	}

	public static JsonElement of(@Nullable Object o) {
		if (o == null) {
			return JsonNull.INSTANCE;
		} else if (o instanceof JsonSerializable serializable) {
			return serializable.toJson();
		} else if (o instanceof JsonElement json) {
			return json;
		} else if (o instanceof CharSequence) {
			return new JsonPrimitive(o.toString());
		} else if (o instanceof Boolean bool) {
			return new JsonPrimitive(bool);
		} else if (o instanceof Number num) {
			return new JsonPrimitive(num);
		} else if (o instanceof Character c) {
			return new JsonPrimitive(c);
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public static Object toObject(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return null;
		} else if (json.isJsonObject()) {
			var map = new LinkedHashMap<String, Object>();
			var o = json.getAsJsonObject();

			for (var entry : o.entrySet()) {
				map.put(entry.getKey(), toObject(entry.getValue()));
			}

			return map;
		} else if (json.isJsonArray()) {
			var a = json.getAsJsonArray();
			List<Object> objects = new ArrayList<>(a.size());

			for (var e : a) {
				objects.add(toObject(e));
			}

			return objects;
		}

		return toPrimitive(json);
	}

	public static String toString(JsonElement json) {
		var writer = new StringWriter();

		try {
			var jsonWriter = new JsonWriter(writer);
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
		var writer = new StringWriter();

		try {
			var jsonWriter = new JsonWriter(writer);
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

	@HideFromJS
	public static JsonElement fromString(@Nullable String string) {
		if (string == null || string.isEmpty() || string.equals("null")) {
			return JsonNull.INSTANCE;
		}

		try {
			var jsonReader = new JsonReader(new StringReader(string));
			JsonElement element;
			var lenient = jsonReader.isLenient();
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

	public static Object parse(String string) {
		return UtilsJS.wrap(fromString(string), JSObjectType.ANY);
	}

	@Nullable
	public static Object toPrimitive(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return null;
		} else if (element.isJsonPrimitive()) {
			var p = element.getAsJsonPrimitive();

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
	public static MapJS read(Path path) throws IOException {
		if (Files.notExists(path)) {
			return null;
		}

		try (var fileReader = Files.newBufferedReader(path)) {
			var jsonReader = new JsonReader(fileReader);
			JsonElement element;
			var lenient = jsonReader.isLenient();
			jsonReader.setLenient(true);
			element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return MapJS.of(element);
		}
	}

	public static void write(Path path, @Nullable JsonObject json) throws IOException {
		if (json == null) {
			Files.deleteIfExists(path);
			return;
		}

		try (Writer fileWriter = Files.newBufferedWriter(path)) {
			var jsonWriter = new JsonWriter(fileWriter);
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
	}

	public static JsonArray toArray(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}

		var a = new JsonArray();
		a.add(element);
		return a;
	}

	public static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		if (element == null || element.isJsonNull()) {
			stream.writeByte('-');
		} else if (element instanceof JsonArray arr) {
			stream.writeByte('[');
			for (var e : arr) {
				writeJsonHash(stream, e);
			}
		} else if (element instanceof JsonObject obj) {
			stream.writeByte('{');
			for (var e : obj.entrySet()) {
				stream.writeBytes(e.getKey());
				writeJsonHash(stream, e.getValue());
			}
		} else if (element instanceof JsonPrimitive primitive) {
			stream.writeByte('=');
			if (primitive.isBoolean()) {
				stream.writeBoolean(element.getAsBoolean());
			} else if (primitive.isNumber()) {
				stream.writeDouble(element.getAsDouble());
			} else {
				stream.writeBytes(element.getAsString());
			}
		} else {
			stream.writeByte('?');
			stream.writeInt(element.hashCode());
		}
	}

	public static byte[] getJsonHashBytes(JsonElement json) {
		var baos = new ByteArrayOutputStream();
		try {
			writeJsonHash(new DataOutputStream(baos), json);
		} catch (IOException ex) {
			ex.printStackTrace();
			var h = json.hashCode();
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) (h >> 0)};
		}

		return baos.toByteArray();
	}
}