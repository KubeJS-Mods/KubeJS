package dev.latvian.kubejs.loot.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.JsonSerializable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LootEntryList extends ArrayList<LootEntry> implements JsonSerializable {
	public boolean add(IngredientJS ingredientJS) {
		LootEntry entry = new LootEntry(ingredientJS);
		return super.add(entry);
	}

	public void add(int index, IngredientJS ingredientJS) {
		LootEntry entry = new LootEntry(ingredientJS);
		super.add(index, entry);
	}

	public LootEntry set(int index, IngredientJS ingredientJS) {
		LootEntry entry = new LootEntry(ingredientJS);
		return super.set(index, entry);
	}

	public boolean add(IngredientJS ingredientJS, Consumer<LootEntry> consumer) {
		LootEntry entry = new LootEntry(ingredientJS);
		consumer.accept(entry);
		return super.add(entry);
	}

	public void add(int index, IngredientJS ingredientJS, Consumer<LootEntry> consumer) {
		LootEntry entry = new LootEntry(ingredientJS);
		consumer.accept(entry);
		super.add(index, entry);
	}

	public LootEntry set(int index, IngredientJS ingredientJS, Consumer<LootEntry> consumer) {
		LootEntry entry = new LootEntry(ingredientJS);
		consumer.accept(entry);
		return super.set(index, entry);
	}

	@Override
	public JsonArray toJson() {
		JsonArray array = new JsonArray();

		forEach(child -> {
			JsonElement element = child.toJson();
			array.add(element);
		});

		return array;
	}
}
