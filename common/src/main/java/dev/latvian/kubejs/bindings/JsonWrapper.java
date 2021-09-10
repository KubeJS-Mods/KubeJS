package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class JsonWrapper {
	public static JsonElement copy(JsonElement json) {
		return JsonUtilsJS.copy(json);
	}

	public static String toString(JsonElement json) {
		return JsonUtilsJS.toString(json);
	}

	public static String toPrettyString(JsonSerializable json) {
		return JsonUtilsJS.toPrettyString(json.toJson());
	}

	public static String toPrettyString(JsonElement json) {
		return JsonUtilsJS.toPrettyString(json);
	}

	public static Object parse(String string) {
		return UtilsJS.wrap(JsonUtilsJS.fromString(string), JSObjectType.ANY);
	}

	@Nullable
	public static MapJS read(File file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	public static void write(File file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}

	@Nullable
	public static MapJS read(String file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	public static void write(String file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}

	public static Object get(JsonObject json, String[] path) {
		if(path.length == 1) {
			return JsonUtilsJS.get(json, path[0].split("\\."));
		}

		return JsonUtilsJS.get(json, path);
	}

	public static boolean equals(JsonObject a, JsonObject b) {
		return JsonUtilsJS.equal(a, b);
	}
}