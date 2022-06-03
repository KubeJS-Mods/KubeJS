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
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonIO {
	@HideFromJS
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

	public static JsonElement copy(@Nullable JsonElement element) {
		return JsonUtils.copy(element);
	}

	public static JsonElement of(@Nullable Object o) {
		if (o instanceof JsonElement) {
			return (JsonElement) o;
		} else if (o instanceof Map || o instanceof CompoundTag) {
			return MapJS.json(o);
		} else if (o instanceof Collection) {
			return ListJS.json(o);
		}

		JsonElement e = JsonUtils.of(o);
		return e == JsonNull.INSTANCE ? null : e;
	}

	public static JsonPrimitive primitiveOf(@Nullable Object o) {
		JsonElement e = of(o);
		return e instanceof JsonPrimitive ? (JsonPrimitive) e : null;
	}

	@Nullable
	public static Object toObject(@Nullable JsonElement json) {
		return JsonUtils.toObject(json);
	}

	public static String toString(JsonElement json) {
		return JsonUtils.toString(json);
	}

	public static String toPrettyString(JsonElement json) {
		return JsonUtils.toPrettyString(json);
	}

	public static JsonElement parseRaw(@Nullable String string) {
		return JsonUtils.fromString(string);
	}

	public static Object parse(String string) {
		return UtilsJS.wrap(parseRaw(string), JSObjectType.ANY);
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

	public static void write(Path path, @Nullable JsonElement json) throws IOException {
		if (json == null || json.isJsonNull()) {
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