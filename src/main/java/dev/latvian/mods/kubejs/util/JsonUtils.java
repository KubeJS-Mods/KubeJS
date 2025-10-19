package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public interface JsonUtils {
	@HideFromJS
	Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().serializeNulls().create();

	MapLike<JsonElement> MAP_LIKE = MapLike.forMap(Map.of(), JsonOps.INSTANCE);

	static JsonElement copy(@Nullable JsonElement element) {
		return switch (element) {
			case null -> JsonNull.INSTANCE;
			case JsonNull jsonNull -> JsonNull.INSTANCE;
			case JsonArray jsonElements -> {
				var a = new JsonArray();

				for (var e : jsonElements) {
					a.add(copy(e));
				}

				yield a;
			}
			case JsonObject jsonObject -> {
				var o = new JsonObject();

				for (var entry : jsonObject.entrySet()) {
					o.add(entry.getKey(), copy(entry.getValue()));
				}

				yield o;
			}
			default -> element;
		};
	}

	static JsonElement of(Context cx, @Nullable Object o) {
		return switch (o) {
			case JsonElement e -> e;
			case JsonSerializable s -> s.toJson(cx);
			case CharSequence ignore -> new JsonPrimitive(o.toString());
			case Boolean b -> new JsonPrimitive(b);
			case Number n -> new JsonPrimitive(n);
			case Character c -> new JsonPrimitive(c);
			case null -> JsonNull.INSTANCE;
			default -> {
				if (cx.isMapLike(o)) {
					yield objectOf(cx, o);
				} else if (cx.isListLike(o)) {
					yield arrayOf(cx, o);
				} else {
					yield JsonNull.INSTANCE;
				}
			}
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
		return switch (array) {
			case JsonArray arr -> arr;
			case CharSequence cs -> {
				try {
					yield JsonUtils.GSON.fromJson(cs.toString(), JsonArray.class);
				} catch (Exception ex) {
					yield null;
				}
			}
			case Iterable<?> itr -> {
				JsonArray json = new JsonArray();

				for (Object o1 : itr) {
					json.add(JsonUtils.of(cx, o1));
				}

				yield json;
			}
			case null, default -> null;
		};
	}

	@Nullable
	static Object toObject(@Nullable JsonElement json) {
		return switch (json) {
			case null -> null;
			case JsonNull jsonNull -> null;
			case JsonObject o -> {
				var map = new LinkedHashMap<String, Object>();

				for (var entry : o.entrySet()) {
					map.put(entry.getKey(), toObject(entry.getValue()));
				}

				yield map;
			}
			case JsonArray a -> {
				var objects = new ArrayList<>(a.size());

				for (var e : a) {
					objects.add(toObject(e));
				}

				yield objects;
			}
			default -> toPrimitive(json);
		};
	}

	static String toString(JsonElement json) {
		return GSON.toJson(json);
	}

	static String toPrettyString(JsonElement json) {
		var writer = new StringWriter();
		var jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("\t");
		GSON.toJson(json, jsonWriter);
		return writer.toString();
	}

	static JsonElement fromString(@Nullable String string) {
		if (string == null || string.isEmpty() || string.equals("null")) {
			return JsonNull.INSTANCE;
		}

		try {
			return GSON.fromJson(string, JsonElement.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	static Object toPrimitive(@Nullable JsonElement element) {
		return switch (element) {
			case JsonPrimitive p -> {
				if (p.isBoolean()) {
					yield p.getAsBoolean();
				} else if (p.isNumber()) {
					yield p.getAsNumber();
				}

				try {
					Double.parseDouble(p.getAsString());
					yield p.getAsNumber();
				} catch (Exception ex) {
					yield p.getAsString();
				}
			}
			case null, default -> null;
		};
	}
}