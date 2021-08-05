package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractLootElementList<E extends LootElement> implements JsonSerializable, Iterable<E> {
	protected final List<E> elements = new ArrayList<>();

	public E remove(int index) {
		return elements.remove(index);
	}

	public boolean remove(String s) {
		return elements.removeIf(condition -> s.equals(condition.getName()));
	}

	public boolean removeIf(Predicate<E> filter) {
		return elements.removeIf(filter);
	}

	public E get(int index) {
		return elements.get(index);
	}

	public E get(String s) {
		return elements.stream()
				.filter(function -> s.equals(function.getName()))
				.findFirst()
				.orElse(null);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public void clear() {
		elements.clear();
	}

	public void forEach(Consumer<? super E> action) {
		elements.forEach(action);
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

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return elements.iterator();
	}
}
