package dev.latvian.kubejs.loot.entry;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.NamedObjectList;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;

public class LootEntryList extends NamedObjectList<AbstractLootEntry> implements JsonSerializable, Iterable<AbstractLootEntry> {

	private final String serializeKey;

	public LootEntryList(String key) {
		this.serializeKey = key;
	}

	public AbstractLootEntry add(IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		add(entry);
		return entry;
	}

	public AbstractLootEntry add(int index, IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		add(index, entry);
		return entry;
	}

	public AbstractLootEntry set(int index, IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		set(index, entry);
		return entry;
	}

	@Override
	protected String getSerializeKey() {
		return serializeKey;
	}
}
