package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Context;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
		return JsonUtils.toPrimitive(element);
	}

	@Nullable
	public static JsonElement readJson(Path path) throws IOException {
		if (Files.notExists(path) || !Files.isRegularFile(path)) {
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
	public static Map<?, ?> read(Context cx, Path path) throws IOException {
		return cx.optionalMapOf(readJson(path));
	}

	public static void write(Path path, @Nullable JsonElement json) throws IOException {
		if (json == null || json instanceof JsonNull) {
			Files.deleteIfExists(path);
		} else {
			Files.writeString(path, JsonUtils.toPrettyString(json));
		}
	}

	public static JsonArray toArray(JsonElement element) {
		return switch (element) {
			case JsonArray a -> a;
			case null, default -> {
				var a = new JsonArray();
				a.add(element);
				yield a;
			}
		};
	}

	public static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		switch (element) {
			case null -> stream.writeByte('-');
			case JsonNull jsonNull -> stream.writeByte('-');
			case JsonArray arr -> {
				stream.writeByte('[');
				for (var e : arr) {
					writeJsonHash(stream, e);
				}
			}
			case JsonObject obj -> {
				stream.writeByte('{');
				for (var e : obj.entrySet()) {
					stream.writeBytes(e.getKey());
					writeJsonHash(stream, e.getValue());
				}
			}
			case JsonPrimitive primitive -> {
				stream.writeByte('=');
				if (primitive.isBoolean()) {
					stream.writeBoolean(element.getAsBoolean());
				} else if (primitive.isNumber()) {
					stream.writeDouble(element.getAsDouble());
				} else {
					stream.writeBytes(element.getAsString());
				}
			}
			default -> {
				stream.writeByte('?');
				stream.writeInt(element.hashCode());
			}
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