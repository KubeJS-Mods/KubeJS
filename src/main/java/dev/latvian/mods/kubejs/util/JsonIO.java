package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;

public class JsonIO {
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

	public static JsonElement readJson(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			return null;
		}

		try (var fileReader = Files.newBufferedReader(path)) {
			return JsonParser.parseReader(fileReader);
		}
	}

	public static String readString(Path path) throws IOException {
		return toString(readJson(path));
	}

	@Nullable
	public static Map<?, ?> read(Path path) throws IOException {
		return MapJS.of(readJson(path));
	}

	public static void write(Path path, @Nullable JsonObject json) throws IOException {
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
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) h};
		}

		return baos.toByteArray();
	}

	public static String getJsonHashString(JsonElement json) {
		try {
			var messageDigest = Objects.requireNonNull(MessageDigest.getInstance("MD5"));
			return new BigInteger(HexFormat.of().formatHex(messageDigest.digest(JsonIO.getJsonHashBytes(json))), 16).toString(36);
		} catch (Exception ex) {
			return "%08x".formatted(json.hashCode());
		}
	}
}