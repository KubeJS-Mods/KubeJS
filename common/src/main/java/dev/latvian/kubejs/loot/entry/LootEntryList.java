package dev.latvian.kubejs.loot.entry;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.loot.AbstractLootElementList;
import dev.latvian.kubejs.util.JsonSerializable;

public class LootEntryList extends AbstractLootElementList<AbstractLootEntry> implements JsonSerializable, Iterable<AbstractLootEntry> {

	private final String serializeKey;

	public LootEntryList(String key) {
		this.serializeKey = key;
	}

	public AbstractLootEntry add(AbstractLootEntry lootEntry) {
		elements.add(lootEntry);
		return lootEntry;
	}

	public AbstractLootEntry add(IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		elements.add(entry);
		return entry;
	}

	public AbstractLootEntry add(int index, IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		elements.add(index, entry);
		return entry;
	}

	public AbstractLootEntry set(int index, IngredientJS ingredientJS) {
		AbstractLootEntry entry = AbstractLootEntry.of(ingredientJS);
		elements.set(index, entry);
		return entry;
	}

	@Override
	protected String getSerializeKey() {
		return serializeKey;
	}
}
