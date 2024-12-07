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
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public interface JsonUtils {
	@HideFromJS
	Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

	static JsonElement copy(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return JsonNull.INSTANCE;
		} else if (element instanceof JsonArray) {
			var a = new JsonArray();

			for (var e : (JsonArray) element) {
				a.add(copy(e));
			}

			return a;
		} else if (element instanceof JsonObject) {
			var o = new JsonObject();

			for (var entry : ((JsonObject) element).entrySet()) {
				o.add(entry.getKey(), copy(entry.getValue()));
			}

			return o;
		}

		return element;
	}

	static JsonElement of(Context cx, @Nullable Object o) {
		return switch (o) {
			case JsonElement e -> e;
			case JsonSerializable s -> s.toJson(cx);
			case CharSequence ignore -> new JsonPrimitive(o.toString());
			case Boolean b -> new JsonPrimitive(b);
			case Number n -> new JsonPrimitive(n);
			case Character c -> new JsonPrimitive(c);
			case Map<?, ?> map -> objectOf(cx, map);
			case CompoundTag tag -> objectOf(cx, tag);
			case Collection<?> c -> arrayOf(cx, c);
			case null, default -> JsonNull.INSTANCE;
		};
	}

	static JsonPrimitive primitiveOf(Context cx, @Nullable Object o) {
		return of(cx, o) instanceof JsonPrimitive p ? p : null;
	}

	@Nullable
	static JsonObject objectOf(Context cx, @Nullable Object map) {
		if (map instanceof JsonObject json) {
			return json;
		} else if (map instanceof CharSequence) {
			try {
				return GSON.fromJson(map.toString(), JsonObject.class);
			} catch (Exception ex) {
				return null;
			}
		}

		var m = (Map<?, ?>) cx.jsToJava(map, TypeInfo.RAW_MAP);

		if (m != null) {
			var json = new JsonObject();

			for (var entry : m.entrySet()) {
				var e = of(cx, entry.getValue());

				if (e instanceof JsonPrimitive p && p.isNumber() && p.getAsNumber() instanceof Double d && d <= Long.MAX_VALUE && d >= Long.MIN_VALUE && d == d.longValue()) {
					var l = d.longValue();

					if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
						json.add(String.valueOf(entry.getKey()), new JsonPrimitive((int) l));
					} else {
						json.add(String.valueOf(entry.getKey()), new JsonPrimitive(l));
					}
				} else {
					json.add(String.valueOf(entry.getKey()), e);
				}
			}

			return json;
		}

		return null;
	}

	@Nullable
	static JsonArray arrayOf(Context cx, @Nullable Object array) {
		if (array instanceof JsonArray arr) {
			return arr;
		} else if (array instanceof CharSequence) {
			try {
				return JsonUtils.GSON.fromJson(array.toString(), JsonArray.class);
			} catch (Exception ex) {
				return null;
			}
		} else if (array instanceof Iterable<?> itr) {
			JsonArray json = new JsonArray();

			for (Object o1 : itr) {
				json.add(JsonUtils.of(cx, o1));
			}

			return json;
		}

		return null;
	}

	@Nullable
	static Object toObject(@Nullable JsonElement json) {
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
			var objects = new ArrayList<>(a.size());

			for (var e : a) {
				objects.add(toObject(e));
			}

			return objects;
		}

		return toPrimitive(json);
	}

	static String toString(JsonElement json) {
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

	static String toPrettyString(JsonElement json) {
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

	static JsonElement fromString(@Nullable String string) {
		if (string == null || string.isEmpty() || string.equals("null")) {
			return JsonNull.INSTANCE;
		}

		try {
			var jsonReader = new JsonReader(new StringReader(string));
			JsonElement element;
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
	static Object toPrimitive(@Nullable JsonElement element) {
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
}