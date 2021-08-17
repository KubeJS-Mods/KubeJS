package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;

public abstract class NamedObjectList<E extends NamedObject> extends ArrayList<E> implements JsonSerializable, Iterable<E> {
	public boolean remove(String s) {
		return removeIf(condition -> s.equals(condition.getName()));
	}

	public E get(String s) {
		return stream()
				.filter(function -> s.equals(function.getName()))
				.findFirst()
				.orElse(null);
	}

	public JsonArray toJson() {
		JsonArray result = new JsonArray();

		forEach(element -> {
			JsonElement json = element.toJson();
			result.add(json);
		});

		return result;
	}

	protected abstract String getSerializeKey();

	@HideFromJS
	public void serializeInto(JsonObject into) {
		if (isEmpty()) {
			return;
		}

		into.add(getSerializeKey(), toJson());
	}
}
