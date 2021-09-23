package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;

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

	public static String toPrettyString(JsonElement json) {
		return JsonUtilsJS.toPrettyString(json);
	}

	public static Object parse(String string) {
		return UtilsJS.wrap(JsonUtilsJS.fromString(string), JSObjectType.ANY);
	}
}